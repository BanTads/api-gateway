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

// ordem: auth, cliente, conta, gerente, saga

app  

// auth registro #R1
.post("/auth", async (req: any, res: any) => {
    try {
      const response = await axios.post(`${authService}`, {
        ...req.body,
      });
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
}) 

// auth login #R2
.post("/auth/login", async (req: any, res: any) => {
    try {
      const response = await axios.post(`${authService}/login`, {
        ...req.body,
      });
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// auth delete
.delete("/auth/:id", async (req: any, res: any) => {
    try {
      const response = await axios.delete(`${authService}/${req.params.id}`, {
        ...req.body,
      });
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// get cliente por id
.get("/clientes/:id", async (req: any, res: Response) => {
    try {
        const response = await axios.get(`${clienteService}/${req.params.id}`);

        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// get clientes
.get("/clientes", async (req: any, res: Response) => {
    try {
        const response = await axios.get(`${clienteService}`, {
        params: req.query,
        });
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// update cliente
.put("/clientes/:id", async (req: any, res: any) => {
    try {
        const response = await axios.put(`${clienteService}/${req.params.id}`, {
        ...req.body,
        });
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// delete cliente
.delete("/clientes/:id", async (req: any, res: Response) => {
    try {
        const response = await axios.delete(`${clienteService}/${req.params.id}`);

        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// editar conta
.put("/conta/:id", async (req: any, res: any) => {
    try {
      const response = await axios.put(`${contaService}/${req.params.id}`, {
        ...req.body,
      });
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// deletar conta
.delete("/conta/:id", async (req: any, res: Response) => {
    try {
        const response = await axios.delete(`${contaService}/${req.params.id}`);

        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// get contas
.get("/contas", async (req: any, res: Response) => {
    try {
        const response = await axios.get(`${contaService}`, {
        params: req.query,
        });

        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// get conta por id
.get("/conta/:id", async (req: any, res: Response) => {
    try {
        const response = await axios.get(`${contaService}/${req.params.id}`, {
        params: req.query,
        });

        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// get conta por id + statement
.get("/conta/:id/statement", async (req: any, res: Response) => {
    try {
        const response = await axios.get(
        `${contaService}/${req.params.id}/statement`,
        {
            params: req.query,
        }
        );

        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// get saldo conta
.get("/conta/:id/balance", async (req: any, res: Response) => {
    try {
        const response = await axios.get(
        `${contaService}/${req.params.id}/balance`
        );

        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})


// deposito #R5
.post("/conta/:id/deposit", async (req: any, res: any) => {
    try {
        const response = await axios.post(
        `${contaService}/${req.params.id}/deposit`,
        {
            ...req.body,
        }
        );
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// saque #R6
.post("/conta/:id/withdraw", async (req: any, res: any) => {
    try {
        const response = await axios.post(
        `${contaService}/${req.params.id}/withdraw`,
        {
            ...req.body,
        }
        );
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// transferencia #R7
.post("/conta/:id/transfer", async (req: any, res: any) => {
    try {
        const response = await axios.post(
        `${contaService}/${req.params.id}/transfer`,
        {
            ...req.body,
        }
        );
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// get user da conta por id
.get("/conta/user/:id", async (req: any, res: any) => {
    try {
        const response = await axios.get(
        `${contaService}/user/${req.params.id}/`
        );
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})


.get("/conta/users/:id", async (req: any, res: any) => {
    try {
        const response = await axios.get(
        `${contaService}/users/${req.params.id}`
        );
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// get gerentes #R19
.get("/gerente", async (req: any, res: Response) => {
    try {
      const response = await axios.get(`${gerenteService}`);
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// get gerente por id
.get("/gerente/:id", async (req: any, res: Response) => {
    try {
      const response = await axios.get(`${gerenteService}/${req.params.id}`);

      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// editar gerente #R20
.put("/gerente/:id", async (req: any, res: any) => {
    try {
      const response = await axios.put(`${gerenteService}/${req.params.id}`, {
        ...req.body,
      });
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// deletar gerente #R18
.delete("/gerente/:id", async (req: any, res: any) => {
    try {
      const response = await axios.delete(`${gerenteService}/${req.params.id}`, {
          ...req.body,
        }
      );
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})

// saga cliente - create
.post("/clientes", async (req: any, res: any) => {
    try {
        const response = await axios.post(`${sagaService}/cliente`, {
        ...req.body,
        });
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// saga gerente - create #R17
.post("/gerente", async (req: any, res: any) => {
    try {
        const response = await axios.post(`${sagaService}/gerente`, {
        ...req.body,
        });
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// saga conta - create
.post("/contas", async (req: any, res: any) => {
    try {
        const response = await axios.post(`${sagaService}/conta`, {
        ...req.body,
        });
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
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