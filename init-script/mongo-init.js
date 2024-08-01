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

print("Removendo todos os documentos da coleção 'usuarios'...");
db.usuarios.deleteMany({});

db.usuarios.insertOne({
  nome: 'Administrador',
  email: 'admin@admin.com',
  senha: '9b11408d58004a5885a2bab75b5ff45fa803dadcbf899290c8b0c51602a5eb18',
  salt: 'FDoCrbm/ZQTDtKghIfZM9A==',
  cargo: 'ADMIN'
});

db.usuarios.insertOne({
  nome: 'Cliente exemplo',
  email: 'cliente@exemplo.com',
  senha: 'a1de33fa9623e80899f0244ee35eb7c04066c0a4052e98f5995db54767d6638d',
  salt: '4aX9mHE3UYa3HiFM1xD0mw==',
  cargo: 'CLIENTE'
});

db.usuarios.insertOne({
  nome: 'Gerente exemplo',
  email: 'gerente@exemplo.com',
  senha: '90227ce5b14e4b91ef1abf9e1b9fa9a431f82d7792ffb87db09389da5fee7418',
  salt: 'PI1CjoaoLS2c9U8jgjUPkw==',
  cargo: 'GERENTE'
});