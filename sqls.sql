CREATE DATABASE cliente

CREATE TABLE clientes (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(255) NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    cpf VARCHAR(11) UNIQUE NOT NULL,
    telefone VARCHAR(20) NOT NULL,
    salario FLOAT NOT NULL,
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

INSERT INTO clientes (
  nome,
  email,
  cpf,
  telefone,
  salario,
  tipo_endereco,
  logradouro,
  numero,
  complemento,
  cep,
  cidade,
  uf
)
VALUES
  (
    'Felipe',
    'felipe@teste.com',
    '12345678444',
    '12345678910',
    5000.00,
    'Residencial',
    'Alto da XV',
    '123',
    'Apto 5',
    '01234567',
    'Curitiba',
    'PR'
  );

