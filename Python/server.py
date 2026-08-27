import asyncio
import json
import websockets

# Conjunto de clientes conectados
connected_clients = set()

#funcion de manejo de conexion 
async def handler(websocket):
    #registro de clientes conectados
    connected_clients.add(websocket)
    client_addr = websocket.remote_address
    print(f"[+] Cliente conectado: {client_addr} | Total: {len(connected_clients)}") #mostrar en la consola el cliente

    try:
        #escuchar mensajes del cliente
        async for message in websocket:
            # convertir el mensaje a json
            data = json.loads(message)
            #mostrar el tipo de mensaje recibido en la consola
            print(f"  Trazo recibido de {client_addr}: {data['type']}")
            #determinar los destinatarios (todos excepto el remitente)
            recipients = connected_clients - {websocket}
            if recipients:
                #enviar el mensaje a todos los destinatarios
                await asyncio.gather(
                    *[client.send(message) for client in recipients]
                )
    #manejo de errores
    except websockets.exceptions.ConnectionClosedError:
        pass
    except Exception as e:
        print(f"[!] Error con {client_addr}: {e}")
    finally:
        connected_clients.discard(websocket)
        print(f"[-] Cliente desconectado: {client_addr} | Total: {len(connected_clients)}")


async def main():
    HOST = "0.0.0.0"
    PORT = 8765

    print(f"Servidor iniciado en ws://{HOST}:{PORT}")
    print("Esperando conexiones...\n")

    async with websockets.serve(handler, HOST, PORT):
        await asyncio.Future()  # Corre indefinidamente


if __name__ == "__main__":
    asyncio.run(main())
