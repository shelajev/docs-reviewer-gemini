# Docs reviewer with Gemini Demo

This demo introduces Docs reviewer with Gemini Demo application.

## Running the application


0. google oauth app should have callback url: `http://localhost:3000/login`
1.  Create a `.env` file in the project root with the necessary environment variables. It should contain the following keys:
    ```dotenv
    GOOGLE_CLIENT_ID=<your_google_client_id>
    GOOGLE_CLIENT_SECRET=<your_google_client_secret>
    GOOGLE_AI_API_KEY=<your_google_ai_api_key>
    ```
    Refer to `.env.example` if available, or consult the application's configuration documentation for details on obtaining these values.
    **Important:** Ensure your Google OAuth application is configured with `http://localhost:3000` as an authorized redirect URI.
2.  Build the application and the container image:
    ```bash
    mvn verify -Dquarkus.container-image.build=true
    ```
3.  Start the application using Docker Compose:
    ```bash
    docker compose up
    ```
4.  Open your browser and navigate to `http://localhost:3000`.




This project is inspired and forked from https://github.com/sberyozkin/quarkus-langchain4j-gemini
