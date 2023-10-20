from fastapi import FastAPI
import uvicorn

app = FastAPI()

@app.get("/")
async def root():
    return {"message": "Finder Microservice"}


@app.get("/finder/{userId}/{concertId}")
async def get_matches(userId, concertId):
    return {
        "message": f"Get matches for concert {concertId} and user {userId}."
    }

if __name__ == "__main__":
    uvicorn.run(app, host="0.0.0.0", port=8011)
