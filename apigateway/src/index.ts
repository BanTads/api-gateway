import express, { response, Response } from "express";
const axios = require("axios").default;
const cors = require("cors");
const app = express();
const port = 3000;

app.use(cors());
app.use(express.json());

const authService = "http://host.docker.internal:8085/api/auth";
const clienteService = "http://host.docker.internal:8081/api/cliente";
const contaService = "http://host.docker.internal:8083/api/conta";
const gerenteService = "http://host.docker.internal:8084/api/gerente";
const sagaService = "http://host.docker.internal:8082/api";

// ordem: auth, cliente, conta, gerente, saga

app

// auth registro #R1
.post("/adicionar", async (req: any, res: any) => {
    try {
      const response = await axios.post(`${authService}/adicionar`, {
        ...req.body,
      });
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
}) 

// auth login #R2
.post("/login", async (req: any, res: any) => {
    try {
      const response = await axios.post(`${authService}/login`, {
        ...req.body,
      });
      return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// get clientes
.get("/listar", async (req: any, res: Response) => {
    try {
        const response = await axios.get(`${clienteService}/listar`, {
        params: req.query,
        });
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// get cliente pelo cpf
.get("/cpf/:cpf", async (req: any, res: Response) => {
    try {
        const response = await axios.get(
            `${clienteService}/cpf/${req.params.cpf}`
        );

        return res.json(response.data);
    } catch (error: any) {
        return res
            .status(error.status ? error.status : 400)
            .json({ ERROR: error.message });
    }
})

//get relatorio de clientes
.get("/relatorio", async (req: any, res: Response) => {
    try {
        const response = await axios.get(
            `${clienteService}/relatorio`
        );

        return res.json(response.data);
    } catch (error: any) {
        return res
            .status(error.status ? error.status : 400)
            .json({ ERROR: error.message });
    }
})

// editar conta
.put("/atualizar/:id", async (req: any, res: any) => {
    try {
      const response = await axios.put(`${contaService}/atualizar/${req.params.id}`, {
        ...req.body,
      });
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// get saldo conta
.get("/saldo/:numeroConta", async (req: any, res: Response) => {
    try {
        const response = await axios.get(
        `${contaService}/saldo/${req.params.id}`
        );

        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// deposito, saque e transferencia #R5, R6 & R7
.post("/transacao", async (req: any, res: any) => {
    try {
        const response = await axios.post(
        `${contaService}/transacao`,
        {
            ...req.body,
        }
        );
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// get extrato
.get("/extrato", async (req: any, res: Response) => {
    try {
        const response = await axios.get(
        `${contaService}/extrato`
        );

        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// get top 3 
.get("/top3/:idGerente", async (req: any, res: any) => {
    try {
        const response = await axios.get(
        `${contaService}/top3/${req.params.idGerente}`);
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

.post("/adicionar", async (req: any, res: any) => {
    try {
      const response = await axios.post(`${gerenteService}/adicionar`, {
        ...req.body,
      });
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
}) 

// get gerentes #R19
.get("/gerente/listar", async (req: any, res: Response) => {
    try {
      const response = await axios.get(`${gerenteService}/listar`);
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// get gerente por id
.get("/listar/:id", async (req: any, res: Response) => {
    try {
      const response = await axios.get(`${gerenteService}/listar/${req.params.id}`);

      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// editar gerente #R20
.put("/atualizar/:id", async (req: any, res: any) => {
    try {
      const response = await axios.put(`${gerenteService}/atualizar/${req.params.id}`, {
        ...req.body,
      });
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// deletar gerente #R18
.delete("/remover/:id", async (req: any, res: any) => {
    try {
      const response = await axios.delete(`${sagaService}/gerente/remover/${req.params.id}`, {
          ...req.body,
        }
      );
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// get gerente por email
.get("/listar/email/:email", async (req: any, res: Response) => {
    try {
      const response = await axios.get(`${gerenteService}/listar/email/${req.params.email}`);

      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// get lista de cliente pendente para aprov.
.get("/pendente-aprovacao/:id", async (req: any, res: Response) => {
    try {
      const response = await axios.get(`${gerenteService}/pendente-aprovacao/${req.params.id}`);

      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// get listagem de todos os clientes
.get("/clientes/:idGerente", async (req: any, res: Response) => {
    try {
      const response = await axios.get(`${gerenteService}/clientes/${req.params.idGerente}`);
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// get dashboard gerentes com  metricas de clientes e saldos
.get("/admin/dashboard", async (req: any, res: Response) => {
    try {
      const response = await axios.get(`${gerenteService}/admin/dashboard`);
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// saga autocadastro
.post("/autocadastro", async (req: any, res: any) => {
    try {
        const response = await axios.post(`${sagaService}/autocadastro`, {
        ...req.body,
        });
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// saga atualiza cliente existente pelo id
.put("/cliente/atualizar/:id", async (req: any, res: Response) => {
    try {
        const response = await axios.put(
            `${sagaService}/cliente/atualizar/${req.params.id}`,
            {
                ...req.body,
            }
        );

        return res.json(response.data);
    } catch (error: any) {
        return res
            .status(error.status ? error.status : 400)
            .json({ ERROR: error.message });
    }
})

// saga gerente - create #R17
.post("/gerente/adicionar", async (req: any, res: any) => {
    try {
        const response = await axios.post(`${sagaService}/adicionar`, {
        ...req.body,
        });
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// saga atualizar gerente por id
.put("/gerente/atualizar/:id", async (req: any, res: Response) => {
    try {
        const response = await axios.put(
            `${sagaService}/gerente/atualizar/${req.params.id}`,
            {
                ...req.body,
            }
        );

        return res.json(response.data);
    } catch (error: any) {
        return res
            .status(error.status ? error.status : 400)
            .json({ ERROR: error.message });
    }
})

// saga deletar gerente por id
.delete("/gerente/:id", async (req: any, res: any) => {
    try {
        const response = await axios.delete(
            `${sagaService}/gerente/${req.params.id}`
        );
        return res.json(response.data);
    } catch (error: any) {
        return res
            .status(error.status ? error.status : 400)
            .json({ ERROR: error.message });
    }
})

  //api port
  app.listen(port, () => {
    return console.log(`Express is listening at http://localhost:${port}`);
  });
  
  //error message log
  app.use((err: Error, res: any) => {
    res.status(500).json({ message: err.message });
  });