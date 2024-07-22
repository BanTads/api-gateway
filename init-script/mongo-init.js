db = db.getSiblingDB('admin');

db.createUser({
  user: 'admin',
  pwd: 'admin',
  roles: [
    {
      role: 'userAdminAnyDatabase',
      db: 'admin',
    },
  ],
});

db = db.getSiblingDB('usuario'); // Nome da base de dados

db.createCollection('usuarios', { // Nome da coleção
  validator: {
    $jsonSchema: {
      bsonType: 'object',
      required: ['nome', 'email', 'senha', 'salt', 'cargo'],
      properties: {
        nome: {
          bsonType: 'string',
          description: 'Nome do usuário deve ser uma string e é obrigatório',
        },
        email: {
          bsonType: 'string',
          pattern: '^.+@.+\..+$',
          description: 'Email deve ser uma string no formato válido e é obrigatório',
        },
        senha: {
          bsonType: 'string',
          description: 'Senha deve ser uma string e é obrigatório',
        },
        salt: {
          bsonType: 'string',
          description: 'Salt deve ser uma string e é obrigatório',
        },
        cargo: {
          bsonType: 'string',
          description: 'Cargo do usuário deve ser uma string e é obrigatório',
        },
      },
    },
  },
});

db.usuarios.insertOne({
  nome: 'Administrador',
  email: 'admin@admin.com',
  senha: '9b11408d58004a5885a2bab75b5ff45fa803dadcbf899290c8b0c51602a5eb18',
  salt: 'FDoCrbm/ZQTDtKghIfZM9A==',
  cargo: 'ADMIN'
});