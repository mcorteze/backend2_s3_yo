
 package com.letrasypapeles.backend.controller;
 
 import com.letrasypapeles.backend.entity.Pedido;
 import com.letrasypapeles.backend.service.PedidoService;
 import org.springframework.beans.factory.annotation.Autowired;
 import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
 import org.springframework.web.bind.annotation.*;
 import java.util.List;
 
 @RestController
 @RequestMapping("/api/pedidos")
 public class PedidoController {
 
     @Autowired
     private PedidoService pedidoService;
 
     @GetMapping
    @PreAuthorize("hasAnyRole('EMPLEADO','GERENTE','ADMIN')")
     public ResponseEntity<List<Pedido>> obtenerTodos() {
         List<Pedido> pedidos = pedidoService.obtenerTodos();
         return ResponseEntity.ok(pedidos);
     }
 
     @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLEADO','GERENTE','ADMIN')")
     public ResponseEntity<Pedido> obtenerPorId(@PathVariable Long id) {
         return pedidoService.obtenerPorId(id)
                 .map(ResponseEntity::ok)
                 .orElse(ResponseEntity.notFound().build());
     }
 
     @GetMapping("/cliente/{clienteId}")
    @PreAuthorize("hasAnyRole('EMPLEADO','GERENTE','ADMIN')")
     public ResponseEntity<List<Pedido>> obtenerPorClienteId(@PathVariable Long clienteId) {
         List<Pedido> pedidos = pedidoService.obtenerPorClienteId(clienteId);
         return ResponseEntity.ok(pedidos);
     }
 
     @PostMapping
    @PreAuthorize("hasAnyRole('EMPLEADO','GERENTE','ADMIN')")
     public ResponseEntity<Pedido> crearPedido(@RequestBody Pedido pedido) {
         Pedido nuevoPedido = pedidoService.guardar(pedido);
         return ResponseEntity.ok(nuevoPedido);
     }
 
     @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLEADO','GERENTE','ADMIN')")
     public ResponseEntity<Pedido> actualizarPedido(@PathVariable Long id, @RequestBody Pedido pedido) {
         return pedidoService.obtenerPorId(id)
                 .map(p -> {
                     pedido.setId(id);
                     Pedido pedidoActualizado = pedidoService.guardar(pedido);
                     return ResponseEntity.ok(pedidoActualizado);
                 })
                 .orElse(ResponseEntity.notFound().build());
     }
 
     @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('EMPLEADO','GERENTE','ADMIN')")
     public ResponseEntity<Void> eliminarPedido(@PathVariable Long id) {
         return pedidoService.obtenerPorId(id)
                 .map(p -> {
                     pedidoService.eliminar(id);
                     return ResponseEntity.ok().<Void>build();
                 })
                 .orElse(ResponseEntity.notFound().build());
     }
 }
 