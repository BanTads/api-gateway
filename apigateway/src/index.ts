import express, { response, Response } from "express";
const axios = require("axios").default;
const cors = require("cors");
const app = express();
const port = 3000;

app.use(cors());
app.use(express.json());

const authService = "http://auth:8012/api/auth";
const clienteService = "http://cliente:8008/api/cliente";
const contaService = "http://conta:8010/api/conta";
const gerenteService = "http://gerente:8011/api/gerente";
const sagaService = "http://saga:8009";

// ordem: auth, cliente, conta, gerente, saga

app  

// auth registro #R1
.post("/adicionar", async (req: any, res: any) => {
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
/*
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
.get("/cliente/:id", async (req: any, res: Response) => {
    try {
        const response = await axios.get(`${clienteService}/${req.params.id}`);

        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})
*/
// get clientes
.get("/listar", async (req: any, res: Response) => {
    try {
        const response = await axios.get(`${clienteService}`, {
        params: req.query,
        });
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// get cliente pelo cpf
.get("/cliente/cpf/:cpf", async (req: any, res: Response) => {
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
.get("/cliente/cpf/:cpf", async (req: any, res: Response) => {
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
/*
// update cliente
.put("/cliente/:id", async (req: any, res: any) => {
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
.delete("/cliente/:id", async (req: any, res: Response) => {
    try {
        const response = await axios.delete(`${clienteService}/${req.params.id}`);

        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})
*/
// editar conta
.put("/atualizar/:id", async (req: any, res: any) => {
    try {
      const response = await axios.put(`${contaService}/${req.params.id}`, {
        ...req.body,
      });
      return res.json(response.data);
    } catch (error: any) {
      return res.status(error.response.status).json(error.response.data);
    }
})
/*
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
*/
// get saldo conta
.get("/saldo/:numeroConta", async (req: any, res: Response) => {
    try {
        const response = await axios.get(
        `${contaService}/${req.params.id}`
        );

        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// deposito, saque e transferencia #R5, R6 & R7
.get("/transacao", async (req: any, res: any) => {
    try {
        const response = await axios.get(
        `${contaService}`,
        {
            ...req.body,
        }
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
        `${contaService}/${req.params.idGerente}`);
        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

// get extrato
.get("/extrato", async (req: any, res: Response) => {
    try {
        const response = await axios.get(
        `${contaService}/${req.params.id}`
        );

        return res.json(response.data);
    } catch (error: any) {
        return res.status(error.response.status).json(error.response.data);
    }
})

/*
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
*/

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
.get("/listar", async (req: any, res: Response) => {
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
      const response = await axios.delete(`${gerenteService}/remover/${req.params.id}`, {
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
      const response = await axios.get(`${gerenteService}/email/${req.params.email}`);

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
/*
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
*/
  //api port
  app.listen(port, () => {
    return console.log(`Express is listening at http://localhost:${port}`);
  });
  
  //error message log
  app.use((err: Error, res: any) => {
    res.status(500).json({ message: err.message });
  });