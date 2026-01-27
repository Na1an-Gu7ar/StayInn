import mysql from "mysql2/promise";

const db = mysql.createPool({
  host: "localhost",
  user: "root",
  password: "MySQL@123",
  database: "villa",
});

export default db;
