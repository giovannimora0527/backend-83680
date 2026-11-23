export class Cliente {
    clienteId?: number;
    usuarioId?: number;
    tipoDocumento?: string;
    numeroDocumento?: string;
    nombres?: string;
    apellidos?: string;
    fechaNacimiento?: string;
    genero?: string;
    telefono?: string;
    direccion?: string;
    activo!: boolean;
}