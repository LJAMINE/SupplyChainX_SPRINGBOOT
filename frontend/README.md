# SupplyChainX Frontend

Angular frontend for the SupplyChainX application.

## Prerequisites

- Node.js 18+
- npm (comes with Node.js)

## Setup

```bash
cd frontend
npm install
```

## Development

```bash
npm start
```

Runs the app at [http://localhost:4200](http://localhost:4200).

**Note:** The backend must be running at `http://localhost:8080` for API calls to work.

## Build

```bash
npm run build
```

## Features

- **Login** – JWT authentication (POST /api/auth/login)
- **Raw Materials CRUD** – List, create, edit, delete (requires authentication)

### Test credentials

- Email: `GESTIONNAIRE_APPROVISIONNEMENT@gmail.com`
- Password: `secret`
