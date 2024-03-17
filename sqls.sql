CREATE DATABASE cliente

CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    salario DECIMAL(10, 2),
    id_endereco INT,
    FOREIGN KEY (id_endereco) REFERENCES enderecos(id)
);


CREATE TABLE enderecos (
    id INT AUTO_INCREMENT PRIMARY KEY,
    tipo VARCHAR(255) NOT NULL,
    logradouro VARCHAR(255) NOT NULL,
    numero VARCHAR(20) NOT NULL,
    complemento VARCHAR(255) NOT NULL,
    cep VARCHAR(8) NOT NULL,
    cidade VARCHAR(50) NOT NULL,
    uf CHAR(2) NOT NULL
);

CREATE DATABASE conta;
CREATE TABLE conta (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT,
    numero_conta VARCHAR(20) UNIQUE NOT NULL,
    data_criacao DATE,
    limite DECIMAL(10, 2),
    id_gerente INT,
    FOREIGN KEY (id_cliente) REFERENCES cliente(id),
    FOREIGN KEY (id_gerente) REFERENCES gerente(id)
);


CREATE TABLE movimentacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    data_hora TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    tipo ENUM('depósito', 'saque', 'transferência'),
    valor DECIMAL(10, 2),
    id_cliente_origem INT,
    id_cliente_destino INT,
    FOREIGN KEY (id_cliente_origem) REFERENCES cliente(id),
    FOREIGN KEY (id_cliente_destino) REFERENCES cliente(id)
);


CREATE DATABASE gerente

CREATE TABLE gerente (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    cpf VARCHAR(14) UNIQUE NOT NULL,
    telefone VARCHAR(20) NOT NULL
);

CREATE DATABASE auth

CREATE TABLE autenticacao (
    id INT AUTO_INCREMENT PRIMARY KEY,
    id_usuario INT,
    tipo ENUM('cliente', 'gerente', 'admin'),
    login VARCHAR(50) UNIQUE NOT NULL,
    senha VARCHAR(100) NOT NULL,
    FOREIGN KEY (id_usuario) REFERENCES cliente(id) ON DELETE CASCADE,
    FOREIGN KEY (id_usuario) REFERENCES gerente(id) ON DELETE CASCADE
);
