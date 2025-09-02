FROM node:22-alpine AS base

# install dependencies
FROM base AS deps
WORKDIR /app

# copy only dependencies to leverage Docker cache
COPY package.json package-lock.json ./
RUN npm ci

# build source code
FROM base AS builder
WORKDIR /app

# reuse node_modules from dep stage
COPY --from=deps /app/node_modules ./node_modules
COPY . .

RUN npm run build

# runtime
FROM base AS runner

# disable telemetry
ENV NEXT_TELEMETRY_DISABLED=1

# create non-root user
RUN addgroup --system --gid 1001 nodejs \
 && adduser --system --uid 1001 nextjs


# copy static assets and standalone server build from builder
COPY --from=builder /app/public ./public
COPY --from=builder --chown=nextjs:nodejs /app/.next/standalone ./
COPY --from=builder --chown=nextjs:nodejs /app/.next/static ./.next/static

# run container as the non-root user
USER nextjs

EXPOSE 3000
ENV PORT=3000

CMD ["node", "server.js"]