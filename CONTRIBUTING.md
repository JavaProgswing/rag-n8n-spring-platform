# Contributing

1. Create a branch from `main`.
2. Make a focused change and include a meaningful test when behavior changes.
3. Run `.\mvnw.cmd verify`.
4. If the deployment stack changed, run `docker compose config`.
5. Open a pull request describing the trigger, resulting behavior, and validation.

Use Java 21. Do not commit `.env`, credentials, database volumes, or model files.
