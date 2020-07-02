# Batch Log Reader Test Project

## Get Started

To get started you should have Docker installed and Maven. This docker compose project includes two maven modules (console 
client and api) in two containers, connected with network. For settings - change console.env config file.
Clone the repo and cd into it, then do: 

```bash
mvn clean package
docker-compose up -d
``` 

There are two directories mounted to your local storage: observed_dir and output_dir. Chmod for this two folders so any user could write and read and execute there recursevely. Put logs to ./observed_dir, the processed output log will appear at ./output_dir
