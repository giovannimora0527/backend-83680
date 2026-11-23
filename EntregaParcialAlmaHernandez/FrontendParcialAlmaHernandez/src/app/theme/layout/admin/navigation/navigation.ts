export interface NavigationItem {
  id: string;
  title: string;
  type: 'item' | 'collapse' | 'group';
  translate?: string;
  icon?: string;
  hidden?: boolean;
  url?: string;
  classes?: string;
  exactMatch?: boolean;
  external?: boolean;
  target?: boolean;
  breadcrumbs?: boolean;
  children?: NavigationItem[];
}

export const NavigationItems: NavigationItem[] = [
  {
    id: 'navigation',
    title: 'Gestión de la Clínica',
    type: 'group',
    icon: 'icon-navigation',
    children: [
      {
        id: 'usuario',
        title: 'Gestión de Usuarios',
        type: 'item',
        url: '/inicio/usuarios',
        icon: 'feather icon-user',
        classes: 'nav-item'
      },
      {
        id: 'pacientes',
        title: 'Gestión de Pacientes',
        type: 'item',
        url: '/inicio/pacientes',
        icon: 'feather icon-users',
        classes: 'nav-item'
      },
      {
        id: 'medicos',
        title: 'Gestión de Médicos',
        type: 'item',
        url: '/inicio/medicos',
        icon: 'feather icon-activity',
        classes: 'nav-item'
      },
      {
        id: 'especializaciones',
        title: 'Gestión de Especializaciones',
        type: 'item',
        url: '/inicio/especializaciones',
        icon: 'feather icon-award',
        classes: 'nav-item'
      },
      {
        id: 'citas',
        title: 'Gestión de Citas',
        type: 'item',
        url: '/inicio/citas',
        icon: 'feather icon-calendar',
        classes: 'nav-item'
      },
      {
        id: 'historias-medicas',
        title: 'Gestión de Historias Médicas',
        type: 'item',
        url: '/inicio/historias-medicas',
        icon: 'feather icon-file-text',
        classes: 'nav-item'
      },
      {
        id: 'anotaciones',
        title: 'Gestión de Anotaciones',
        type: 'item',
        url: '/inicio/anotaciones',
        icon: 'feather icon-edit',
        classes: 'nav-item'
      },
      {
        id: 'medicamentos',
        title: 'Gestión de Medicamentos',
        type: 'item',
        url: '/inicio/medicamentos',
        icon: 'feather icon-plus-square',
        classes: 'nav-item'
      },
      {
        id: 'formulas-medicas',
        title: 'Gestión de Fórmulas Médicas',
        type: 'item',
        url: '/inicio/formulas-medicas',
        icon: 'feather icon-clipboard',
        classes: 'nav-item'
      },
      {
        id: 'despacho',
        title: 'Despacho Médico',
        type: 'item',
        url: '/inicio/despacho',
        icon: 'feather icon-package',
        classes: 'nav-item'
      },
      {
        id: 'cliente',
        title: 'Gestión de Clientes',
        type: 'item',
        url: '/inicio/clientes',
        icon: 'feather icon-user',
        classes: 'nav-item'
      },
      {
        id: 'mascota',
        title: 'Gestión de Mascotas',
        type: 'item',
        url: '/inicio/mascotas',
        icon: 'feather icon-heart',
        classes: 'nav-item'
      }
    ]
  }
];
