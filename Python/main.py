import tkinter as tk
import asyncio
import json
import threading
import websockets

SERVER_URL = "ws://192.168.20.8:8765"

class App:
    def __init__(self):
        self.root = tk.Tk()
        self.root.title("Interfaz - PC")
        self.root.geometry("400x300")
        self.root.configure(bg="#F5F5F5")
        self.root.resizable(False, False)

        self.connection_mode = False
        self.websocket = None
        self.color_actual = "#212121"
        self.last_x = None
        self.last_y = None
        self.anim_running = False
        self._loop = None

        self.show_main_screen()
        self.root.mainloop()

    def clear_window(self):
        for widget in self.root.winfo_children():
            widget.destroy()

    # ── Pantalla principal ────────────────────────────────────

    def show_main_screen(self):
        self.clear_window()
        self.root.geometry("400x300")
        self.root.resizable(False, False)

        tk.Label(self.root, text="Dibujo y animacion",
                 font=("Arial", 22, "bold"),
                 bg="#F5F5F5", fg="#212121").pack(pady=(50, 5))

        tk.Label(self.root, text="Selecciona el modo de uso",
                 font=("Arial", 12),
                 bg="#F5F5F5", fg="#757575").pack(pady=(0, 40))

        tk.Button(self.root, text="Modo Conexión",
                  font=("Arial", 13),
                  bg="#1565C0", fg="white",
                  relief="flat", padx=20, pady=12,
                  cursor="hand2",
                  command=self.modo_conexion).pack(fill=tk.X, padx=60, pady=5)

        tk.Button(self.root, text="Modo Sin Conexión",
                  font=("Arial", 13),
                  bg="white", fg="#212121",
                  relief="flat", padx=20, pady=12,
                  cursor="hand2",
                  command=self.modo_sin_conexion).pack(fill=tk.X, padx=60, pady=5)

    def modo_conexion(self):
        self.connection_mode = True
        self.start_websocket()
        self.show_menu_screen()

    def modo_sin_conexion(self):
        self.connection_mode = False
        self.show_menu_screen()

    # ── Pantalla menú ─────────────────────────────────────────

    def show_menu_screen(self):
        self.anim_running = False
        self.clear_window()
        self.root.geometry("400x300")
        self.root.resizable(False, False)

        mode_text = "Modo Conexión activo" if self.connection_mode else "Modo Sin Conexión"
        mode_color = "#1565C0" if self.connection_mode else "#757575"

        tk.Label(self.root, text=mode_text,
                 font=("Arial", 11),
                 bg="#F5F5F5", fg=mode_color).pack(pady=(30, 20))

        tk.Button(self.root, text="Dibujar",
                  font=("Arial", 14),
                  bg="#212121", fg="white",
                  relief="flat", padx=20, pady=12,
                  cursor="hand2",
                  command=self.pantalla_dibujo).pack(fill=tk.X, padx=60, pady=6)

        tk.Button(self.root, text="Animar",
                  font=("Arial", 14),
                  bg="#212121", fg="white",
                  relief="flat", padx=20, pady=12,
                  cursor="hand2",
                  command=self.pantalla_animacion).pack(fill=tk.X, padx=60, pady=6)

        tk.Button(self.root, text="Cerrar",
                  font=("Arial", 14),
                  bg="#C62828", fg="white",
                  relief="flat", padx=20, pady=12,
                  cursor="hand2",
                  command=self.cerrar).pack(fill=tk.X, padx=60, pady=6)

    def cerrar(self):
        self.websocket = None
        self.root.destroy()

    # ── Pantalla dibujo ───────────────────────────────────────

    def pantalla_dibujo(self):
        self.clear_window()
        self.root.geometry("800x600")
        self.root.resizable(True, True)

        toolbar = tk.Frame(self.root, bg="#EEEEEE", pady=8)
        toolbar.pack(fill=tk.X)

        if self.connection_mode:
            estado = "Conectado" if self.websocket else "Desconectado"
            color = "#388E3C" if self.websocket else "#C62828"
        else:
            estado = "Sin conexión"
            color = "#757575"

        tk.Label(toolbar, text=estado,
                 bg="#EEEEEE", fg=color,
                 font=("Arial", 10)).pack(side=tk.LEFT, padx=10)

        tk.Button(toolbar, text="✕ Limpiar",
                  bg="#F44336", fg="white",
                  relief="flat", padx=10,
                  cursor="hand2",
                  command=self._clear_canvas).pack(side=tk.RIGHT, padx=10)

        tk.Button(toolbar, text="← Volver",
                  bg="#757575", fg="white",
                  relief="flat", padx=10,
                  cursor="hand2",
                  command=self.show_menu_screen).pack(side=tk.RIGHT, padx=4)

        self.canvas = tk.Canvas(self.root, bg="white", cursor="crosshair")
        self.canvas.pack(fill=tk.BOTH, expand=True)

        self.last_x = None
        self.last_y = None
        self.canvas.bind("<ButtonPress-1>", self._on_press)
        self.canvas.bind("<B1-Motion>", self._on_drag)
        self.canvas.bind("<ButtonRelease-1>", self._on_release)

    def _on_press(self, event):
        self.last_x = event.x
        self.last_y = event.y
        self.canvas.focus_set()

    def _on_drag(self, event):
        if self.last_x is not None and self.last_y is not None:
            self.canvas.create_line(self.last_x, self.last_y,
                                    event.x, event.y,
                                    fill=self.color_actual,
                                    width=4,
                                    capstyle=tk.ROUND,
                                    smooth=True)
            if self.connection_mode and self.websocket:
                self._send_stroke(self.last_x, self.last_y,
                                  event.x, event.y,
                                  self.color_actual, 4)
            self.last_x = event.x
            self.last_y = event.y

    def _on_release(self, event):
        self.last_x = None
        self.last_y = None

    def _clear_canvas(self):
        self.canvas.delete("all")
        if self.connection_mode and self.websocket:
            self._send_clear()

    # ── Pantalla animación ────────────────────────────────────

    def pantalla_animacion(self):
        self.clear_window()
        self.root.geometry("800x600")
        self.root.resizable(True, True)

        toolbar = tk.Frame(self.root, bg="#EEEEEE", pady=8)
        toolbar.pack(fill=tk.X)

        tk.Label(toolbar, text="Animación — Parábola",
                 font=("Arial", 13, "bold"),
                 bg="#EEEEEE", fg="#212121").pack(side=tk.LEFT, padx=10)

        tk.Button(toolbar, text="← Volver",
                  bg="#757575", fg="white",
                  relief="flat", padx=10,
                  cursor="hand2",
                  command=self._stop_animation).pack(side=tk.RIGHT, padx=10)

        self.anim_canvas = tk.Canvas(self.root, bg="white")
        self.anim_canvas.pack(fill=tk.BOTH, expand=True)

        self.anim_t = 0.0
        self.anim_forward = True
        self.anim_running = True

        # Avisar al celular solo si no fue el celular quien lo inició
        if self.connection_mode and self.websocket:
            self._send_message({"type": "start_animation"})

        self.root.after(16, self._animate)

    def _animate(self):
        if not self.anim_running:
            return

        self.anim_canvas.delete("all")

        w = self.anim_canvas.winfo_width()
        h = self.anim_canvas.winfo_height()

        if w < 10 or h < 10:
            self.root.after(16, self._animate)
            return

        x = self.anim_t * w
        y = h - (4 * h * self.anim_t * (1 - self.anim_t))

        r = 25
        self.anim_canvas.create_oval(x - r, y - r, x + r, y + r,
                                      fill="blue", outline="")

        speed = 0.005
        if self.anim_forward:
            self.anim_t += speed
            if self.anim_t >= 1.0:
                self.anim_forward = False
        else:
            self.anim_t -= speed
            if self.anim_t <= 0.0:
                self.anim_forward = True

        self.root.after(16, self._animate)

    def _stop_animation(self):
        self.anim_running = False
        if self.connection_mode and self.websocket:
            self._send_message({"type": "stop_animation"})
        self.show_menu_screen()

    def _send_message(self, data):
        msg = json.dumps(data)
        asyncio.run_coroutine_threadsafe(self.websocket.send(msg), self._loop)

    # ── WebSocket ─────────────────────────────────────────────

    def start_websocket(self):
        thread = threading.Thread(target=self._ws_thread, daemon=True)
        thread.start()

    def _ws_thread(self):
        self._loop = asyncio.new_event_loop()
        asyncio.set_event_loop(self._loop)
        self._loop.run_until_complete(self._ws_connect())

    async def _ws_connect(self):
        try:
            async with websockets.connect(SERVER_URL) as ws:
                self.websocket = ws
                print("Conectado al servidor")
                async for message in ws:
                    self._on_message(json.loads(message))
        except Exception as e:
            print(f"Error de conexión: {e}")
            self.websocket = None

    def _on_message(self, data):
        if data["type"] == "stroke":
            self.root.after(0, lambda: self.canvas.create_line(
                data["x1"], data["y1"],
                data["x2"], data["y2"],
                fill=data["color"], width=data["size"],
                capstyle=tk.ROUND, smooth=True
            ))
        elif data["type"] == "clear":
            self.root.after(0, lambda: self.canvas.delete("all"))
        elif data["type"] == "start_animation":
            self.root.after(0, self.pantalla_animacion)
        elif data["type"] == "stop_animation":
            self.root.after(0, self._stop_animation)

    def _send_stroke(self, x1, y1, x2, y2, color, size):
        msg = json.dumps({
            "type": "stroke",
            "x1": x1, "y1": y1,
            "x2": x2, "y2": y2,
            "color": color,
            "size": size
        })
        asyncio.run_coroutine_threadsafe(self.websocket.send(msg), self._loop)

    def _send_clear(self):
        msg = json.dumps({"type": "clear"})
        asyncio.run_coroutine_threadsafe(self.websocket.send(msg), self._loop)


if __name__ == "__main__":
    App()