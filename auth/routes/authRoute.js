import { Router } from "express";
import { loginController, signupController } from "../controller/authController.js";

const authRoute = Router();

authRoute.post("/signup", signupController);
authRoute.post("/login", loginController);

export default authRoute;
