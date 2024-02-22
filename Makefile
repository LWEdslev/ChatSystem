run:
	@gradle run

.PHONY: frontend
frontend:
	@cd frontend && npm run build && cd ..

full:
	@cd frontend && npm run build && cd .. && gradle run

docker-build:
	@sudo docker build -t chat .

docker:
	@sudo docker run -p 8080:8080 chat