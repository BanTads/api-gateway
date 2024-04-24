CREATE DATABASE cliente;
\c cliente;

CREATE TABLE public.enderecos (
    id SERIAL PRIMARY KEY,
    tipo VARCHAR(255) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    complemento VARCHAR(255) NOT NULL,
    cep VARCHAR(10) NOT NULL,
    cidade VARCHAR(50) NOT NULL,
    uf CHAR(2) NOT NULL
);

CREATE TABLE public.clientes (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    salario DECIMAL(10, 2),
    id_endereco INT,
    FOREIGN KEY (id_endereco) REFERENCES enderecos(id)
);

CREATE DATABASE conta;
\c conta;

CREATE TABLE public.contas (
    id SERIAL PRIMARY KEY,
    aprovada boolean,
    id_cliente INT,
    data_criacao DATE,
    limite DECIMAL(10, 2),
    id_gerente INT
);

CREATE TYPE public.tipo_movimentacao AS ENUM ('depósito', 'saque', 'transferência');

CREATE TABLE public.movimentacao (
    id SERIAL PRIMARY KEY,
    data_hora TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    tipo tipo_movimentacao,
    valor DECIMAL(10, 2),
    id_conta_origem INT,
    id_conta_destino INT,
    CONSTRAINT fk_conta_origem FOREIGN KEY (id_conta_origem) REFERENCES public.contas(id),
    CONSTRAINT fk_conta_destino FOREIGN KEY (id_conta_destino) REFERENCES public.contas(id)
);