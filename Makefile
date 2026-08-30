.RECIPEPREFIX = >
.PHONY: install run backend frontend clean

install:
> cd backend && python3 -m venv .venv && .venv/bin/pip install -r requirements.txt
> cd frontend && npm install

run:
> @trap 'kill 0' EXIT INT TERM; \
> (cd backend && .venv/bin/uvicorn main:app --reload --port 8000) & \
> (cd frontend && npm run dev) & \
> wait

backend:
> cd backend && .venv/bin/uvicorn main:app --reload --port 8000

frontend:
> cd frontend && npm run dev

clean:
> rm -rf backend/.venv frontend/node_modules
