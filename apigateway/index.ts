import express, { response, Response } from "express";
const axios = require("axios").default;
const cors = require("cors");
const app = express();
const port = 3000;

app.use(cors());
app.use(express.json());

const authService = "http://auth:port/auth";
const sagaService = "http://saga:port";
const clienteService = "http://cliente:port/cliente";
const gerenteService = "http://gerente:port/gerente";
const contaService = "http://conta:port/conta";
