# ---------- BUILD STAGE ----------
FROM gradle:8.14.0-jdk21 AS builder

WORKDIR /app

# Copy Gradle files first (for cache)
COPY build.gradle settings.gradle ./
COPY gradle ./gradle

RUN gradle dependencies --no-daemon

# Copy source and build
COPY src ./src
RUN gradle bootJar --no-daemon


# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre

# Install system dependencies required by Playwright
# Using updated package names for newer Ubuntu
RUN apt-get update && apt-get install -y \
    libnss3 \
    libatk-bridge2.0-0 \
    libatk1.0-0 \
    libcups2 \
    libxkbcommon0 \
    libxcomposite1 \
    libxrandr2 \
    libxdamage1 \
    libgbm1 \
    libasound2t64 \
    libpangocairo-1.0-0 \
    libpango-1.0-0 \
    libgtk-3-0 \
    libx11-xcb1 \
    libxshmfence1 \
    ca-certificates \
    fonts-liberation \
    wget \
    && rm -rf /var/lib/apt/lists/*

# Install Playwright dependencies
RUN wget -q -O - https://dl.google.com/linux/linux_signing_key.pub | apt-key add - && \
    echo "deb [arch=amd64] http://dl.google.com/linux/chrome/deb/ stable main" >> /etc/apt/sources.list.d/google.list && \
    apt-get update && \
    apt-get install -y google-chrome-stable fonts-ipafont-gothic fonts-wqy-zenhei fonts-thai-tlwg fonts-kacst fonts-freefont-ttf libxtst6 libxss1 --no-install-recommends && \
    rm -rf /var/lib/apt/lists/* \

WORKDIR /app

# Copy jar from build stage
COPY --from=builder /app/build/libs/*.jar app.jar

# Directory for generated certificates
RUN mkdir -p /app/certificates
RUN mkdir -p /ms-playwright

ENV PLAYWRIGHT_BROWSERS_PATH=/ms-playwright
ENV PLAYWRIGHT_SKIP_BROWSER_DOWNLOAD=1

EXPOSE 8080

COPY entrypoint.sh /entrypoint.sh
RUN chmod +x /entrypoint.sh
ENTRYPOINT ["/entrypoint.sh"]

