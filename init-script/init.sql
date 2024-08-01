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

INSERT INTO public.enderecos (tipo, logradouro, numero, complemento, cep, cidade, uf)
VALUES ('Residencial', 'Rua Exemplo', '123', 'Apto 456', '12345-678', 'Cidade Exemplo', 'EX');


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

INSERT INTO public.clientes (nome, email, cpf, telefone, salario, id_endereco)
VALUES ('Cliente exemplo', 'cliente@exemplo.com', '123.456.789-00', '(12) 12345-1234', 5000.00, 1);

CREATE DATABASE conta;
\c conta;

CREATE TABLE public.contas (
    numero_conta SERIAL primary key,
    aprovada boolean,
    id_cliente INT,
    data_criacao DATE,
    motivo VARCHAR(255),
    limite DECIMAL(10, 2),
    id_gerente INT
);

CREATE TYPE public.tipo_movimentacao AS ENUM ('DEPOSITO', 'SAQUE', 'TRANSFERENCIA');

CREATE TABLE public.movimentacao (
    id SERIAL PRIMARY KEY,
    data_hora TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    tipo tipo_movimentacao,
    valor DECIMAL(10, 2),
    id_conta_origem INT,
    id_conta_destino INT,
    CONSTRAINT fk_conta_origem FOREIGN KEY (id_conta_origem) REFERENCES public.contas(numero_conta),
    CONSTRAINT fk_conta_destino FOREIGN KEY (id_conta_destino) REFERENCES public.contas(numero_conta)
);

CREATE DATABASE gerente;
\c gerente;
CREATE TABLE public.gerentes (
    id SERIAL PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    quantidade_contas integer
);

INSERT INTO public.gerentes (nome, email, cpf, telefone, quantidade_contas)
VALUES ('Gerente Exemplo', 'gerente@exemplo.com', '123.456.789-00', '(12) 12345-1234', 0);