import bcrypt from "bcryptjs";
import jwt from "jsonwebtoken";
import db from "../dbConfig/dbConfig.js";

export const signupController = async (req, res) => {
  try {
    const { name, email, password, mobile, role } = req.body;

    // 1️⃣ Basic validation
    if (!name || !email || !password || !mobile) {
      return res.status(400).json({
        message: "All fields are required",
      });
    }

    // 2️⃣ Check existing user (email OR mobile)
    const [exists] = await db.query(
      "SELECT 1 FROM users WHERE email = ? OR mobile = ? LIMIT 1",
      [email, mobile]
    );

    if (exists.length > 0) {
      return res.status(400).json({
        message: "User already exists with this email or mobile",
      });
    }

    // 3️⃣ Hash password
    const hashedPassword = await bcrypt.hash(password, 10);

    // 4️⃣ Role validation (SECURITY IMPORTANT)
    const userRole = role === "ADMIN" ? "ADMIN" : "USER";

    // 5️⃣ Insert user
    await db.query(
      "INSERT INTO users (name, email, mobile, password, role) VALUES (?, ?, ?, ?, ?)",
      [name, email, mobile, hashedPassword, userRole]
    );

    res.status(201).json({
      message: "User registered successfully",
    });
  } catch (error) {
    res.status(500).json({
      message: "Signup failed",
      error: error.message,
    });
  }
};

/**
 * LOGIN
 */
export const loginController = async (req, res) => {
  try {
    const { email, password } = req.body;

    const [users] = await db.query("SELECT * FROM users WHERE email = ?", [
      email,
    ]);

    if (users.length === 0) {
      return res.status(404).json({ message: "User not found" });
    }

    const user = users[0];

    // Compare password
    const isMatch = await bcrypt.compare(password, user.password);
    if (!isMatch) {
      return res.status(401).json({ message: "Invalid credentials" });
    }

    // JWT payload
    const token = jwt.sign(
      {
        id: user.id,
        email: user.email,
        role: user.role,
      },
      process.env.JWT_SECRET,
      { expiresIn: "1h" }
    );

    // Cookie
    res.cookie("token", token, {
      httpOnly: true,
      sameSite: "strict",
      secure: false,
    });

    res.status(200).json({
      message: "Login successful",
      token,
      role: user.role,
    });
  } catch (error) {
    res.status(500).json({ message: "Login failed", error: error.message });
  }
};
