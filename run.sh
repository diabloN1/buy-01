set -a
source .env
set +a

sudo docker compose down -v
sudo docker compose up --build