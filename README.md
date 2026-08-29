# Aplicación Multiplataforma Cliente-Servidor

Proyecto académico desarrollado para la materia **Desarrollo de Apps Multiplataforma**. El proyecto implementa comunicación en tiempo real entre clientes Android y Python mediante WebSockets.

## Descripción

La aplicación está compuesta por un servidor desarrollado en Python y clientes desarrollados en Android y Python.

Los usuarios pueden conectarse al servidor para compartir dibujos y visualizar una animación sincronizada entre los clientes conectados.

## Funcionalidades

* Conexión entre múltiples clientes mediante WebSockets.
* Dibujo colaborativo en tiempo real.
* Visualización de trazos realizados por otros usuarios.
* Animación sincronizada entre los clientes.
* Cliente Android.
* Cliente Python para PC.
* Comunicación mediante mensajes en formato JSON.

## Arquitectura

```text
                 ┌─────────────────────┐
                 │   Servidor Python   │
                 │      WebSocket      │
                 └──────────┬──────────┘
                            │
              ┌─────────────┼─────────────┐
              │             │             │
              ▼             ▼             ▼
       Android Cliente  Android Cliente  Python Cliente
              │             │             │
              └──────── Comunicación ────┘
                    en tiempo real
```

## Tecnologías

* Python
* Android Studio
* Java/Kotlin
* WebSockets
* JSON
* Asyncio

## Estructura del proyecto

```text
App-Multiplataforma-Cliente-Servidor/
│
├── Python/
│   ├── server.py
│   └── main.py
│ 
│  
│       
│
├── Android/
│
│
│
└── README.md
```

## Requisitos

### Python

Se requiere Python 3.

### Android

Se requiere:

* Android Studio.
* Android SDK.
* Dispositivo Android o emulador compatible.

## Ejecución

### Servidor Python

Ingresar a la carpeta del servidor y ejecutar:

```bash
python server.py
```

El servidor utiliza:

```text
Host: 0.0.0.0
Puerto: 8765
```

Los clientes deben conectarse utilizando la dirección IP del equipo donde se encuentra ejecutándose el servidor:

```text
ws://IP_DEL_SERVIDOR:8765
```

### Cliente Python

Ejecutar el programa del cliente desde su carpeta:

```bash
python main.py
```

### Cliente Android

Abrir el proyecto ubicado en la carpeta `Android/` utilizando Android Studio, compilar la aplicación y ejecutarla en un dispositivo Android o emulador.

Para conectarse al servidor, el dispositivo debe poder comunicarse con el equipo donde se está ejecutando el servidor.

## Comunicación

El servidor utiliza WebSockets para mantener las conexiones con los clientes.

Los mensajes se manejan mediante **JSON**. Cuando un cliente envía información, el servidor la recibe y la distribuye a los demás clientes conectados.

Esto permite compartir los trazos realizados y mantener sincronizada la información entre los diferentes usuarios.

## Objetivo

Practicar conceptos relacionados con:

* Arquitectura cliente-servidor.
* Comunicación en tiempo real.
* WebSockets.
* Programación asíncrona.
* Intercambio de información mediante JSON.
* Desarrollo de aplicaciones Android.
* Comunicación entre diferentes plataformas.

## Nota

Proyecto desarrollado con fines académicos como parte de la materia **Desarrollo de Apps Multiplataforma**.
