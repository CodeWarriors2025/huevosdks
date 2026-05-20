# HUEVOS DKS

Sistema web para la gestión de pedidos de huevos a domicilio, desarrollado como proyecto formativo SENA.

## Descripción general

HUEVOS DKS permite administrar productos, inventario, clientes, pedidos y entregas mediante roles diferenciados.

El sistema cuenta con tres roles principales:

- ADMIN
- CLIENTE
- OPERADOR

## Funcionalidades principales

### Cliente

- Registro público de cliente.
- Inicio de sesión.
- Consulta de catálogo.
- Agregar productos al carrito.
- Validación de pedido mínimo de 6 huevos.
- Creación de pedidos.
- Consulta de mis pedidos.
- Actualización de datos de perfil y dirección de entrega.

### Administrador

- Panel administrativo.
- Gestión de productos.
- Gestión de inventario.
- Gestión de pedidos.
- Cambio de estado de pedidos.
- Gestión de usuarios y clientes.
- Creación controlada de operadores.
- Activación y desactivación de usuarios.
- Reportes básicos administrativos.

### Operador

- Inicio de sesión.
- Consulta de pedidos en ruta.
- Marcación de pedidos como entregados.
- Consulta de perfil.

## Tecnologías utilizadas

- Java 17
- Spring Boot
- Spring Security
- Spring Data JPA
- Thymeleaf
- Bootstrap 5
- MySQL
- Maven
- Git y GitHub

## Estructura principal

```text
src/main/java/com/huevosdks
├── config
├── controller
├── dto
├── entity
├── repository
└── servicesrc/main/resources/templates
├── error
├── fragments
├── admin-dashboard.html
├── admin-pedidos.html
├── admin-productos.html
├── admin-reportes.html
├── admin-usuarios.html
├── carrito.html
├── catalogo.html
├── login.html
├── mis-pedidos.html
├── operador-pedidos.html
├── pedido-confirmado.html
├── perfil.html
└── registro.html
