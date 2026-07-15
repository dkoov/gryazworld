FROM node:22-alpine

WORKDIR /app

RUN corepack enable && corepack prepare pnpm@10.28.0 --activate

COPY package.json ./
RUN pnpm install --prod=false

COPY src ./src

CMD ["pnpm", "start"]
