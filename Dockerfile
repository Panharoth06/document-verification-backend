# syntax=docker/dockerfile:1

# ==============================================================================
# Stage 1: Build & Pre-download Playwright Chromium Browser
# ==============================================================================
FROM gradle:8.14.0-jdk21 AS builder

WORKDIR /app

# Optimize layer caching: copy build definition and wrapper first
COPY gradle/ ./gradle/
COPY gradlew build.gradle settings.gradle ./

# Cache Gradle dependencies
RUN gradle dependencies --no-daemon

# Copy application source code
COPY src/ ./src/

# Pre-download Playwright Chromium browser binaries into a shared path
ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright
RUN gradle playwrightInstall --no-daemon

# Build Spring Boot executable fat JAR (skipping unit tests during packaging)
RUN gradle bootJar -x test --no-daemon

# Prepare application jar
RUN cp build/libs/*.jar app.jar


# ==============================================================================
# Stage 2: Minimal & Secure Production Runtime
# ==============================================================================
FROM eclipse-temurin:21-jre-jammy AS runner

# Application configuration & JVM tuning
ENV APPLICATION_USER=appuser \
    APPLICATION_GROUP=appgroup \
    APP_HOME=/app \
    PLAYWRIGHT_BROWSERS_PATH=/ms-playwright \
    PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1 \
    JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# Install Chromium system dependencies & font rendering libraries for Ubuntu Jammy
RUN apt-get update && apt-get install -y --no-install-recommends \
    ca-certificates \
    fontconfig \
    fonts-liberation \
    fonts-dejavu-core \
    libasound2 \
    libatk-bridge2.0-0 \
    libatk1.0-0 \
    libcairo2 \
    libcups2 \
    libdbus-1-3 \
    libdrm2 \
    libgbm1 \
    libglib2.0-0 \
    libnspr4 \
    libnss3 \
    libpango-1.0-0 \
    libx11-6 \
    libx11-xcb1 \
    libxcb1 \
    libxcomposite1 \
    libxdamage1 \
    libxext6 \
    libxfixes3 \
    libxkbcommon0 \
    libxrandr2 \
    wget \
    && rm -rf /var/lib/apt/lists/*

WORKDIR ${APP_HOME}

# Create non-root user and setup directories
RUN groupadd -g 1001 ${APPLICATION_GROUP} && \
    useradd -u 1001 -g ${APPLICATION_GROUP} -m -s /bin/bash ${APPLICATION_USER} && \
    mkdir -p ${APP_HOME}/certificates ${PLAYWRIGHT_BROWSERS_PATH}

# Copy Playwright browsers pre-installed during the build stage
COPY --from=builder /ms-playwright ${PLAYWRIGHT_BROWSERS_PATH}

# Copy compiled application JAR
COPY --from=builder /app/app.jar ${APP_HOME}/app.jar

# Setup entrypoint script
COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh && \
    chown -R ${APPLICATION_USER}:${APPLICATION_GROUP} ${APP_HOME} ${PLAYWRIGHT_BROWSERS_PATH}

# Switch to non-root user for security and Chromium sandbox compliance
USER ${APPLICATION_USER}

EXPOSE 8080

# Health check using springdoc OpenAPI endpoint
HEALTHCHECK --interval=20s --timeout=5s --start-period=30s --retries=3 \
  CMD wget -q -O - http://localhost:8080/api-docs > /dev/null 2>&1 || exit 1

ENTRYPOINT ["/entrypoint.sh"]
CMD ["java", "-jar", "app.jar"]
