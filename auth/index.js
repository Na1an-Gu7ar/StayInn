import express from "express";
import cookieParser from "cookie-parser";
import cors from "cors";
import dotenv from "dotenv";
import authRoute from "./routes/authRoute.js";
import { Router } from "express";
import { protect } from "./middleware/authMiddleware.js";
import { authorizeRoles } from "./middleware/roleMiddleware.js";

dotenv.config();

const app = express();
const port = process.env.PORT || 5000;

app.use(cookieParser());
app.use(express.json());
app.use(cors());
const router = Router();

// Any logged-in user
router.get("/profile", protect, (req, res) => {
  res.json({
    message: "User profile",
    user: req.user,
  });
});

// Only ADMIN
router.get("/admin", protect, authorizeRoles("ADMIN"), (req, res) => {
  res.json({ message: "Welcome Admin" });
});

app.get("/", (req, res) => {
  res.send("Hello World!");
});

app.use("/api/users", authRoute);
app.use("/api", router);

app.listen(port, () => {
  console.log(`Server is running on port ${port}`);
});
