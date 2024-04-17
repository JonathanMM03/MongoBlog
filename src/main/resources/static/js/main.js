const usuarioKey = 'usuario';
const usuarioInfo = sessionStorage.getItem(usuarioKey);

/**
 * Función para manejar el logout de usuario
 */
function logout() {
    // Obtener el formulario de logout por su ID
    var logoutForm = document.getElementById('logoutForm');

    // Enviar el formulario
    if (logoutForm) {
        logoutForm.submit();
    }
}

/**
 * Función asíncrona para cargar y mostrar las publicaciones
 */
async function cargarPublicaciones() {
    try {
        const response = await fetch('/publicaciones/all');
        if (!response.ok) {
            throw new Error('Error al cargar las publicaciones');
        }
        const publicaciones = await response.json();
        const publicacionesList = document.getElementById('publicacionesList');

        // Limpiar la lista antes de agregar nuevas publicaciones
        publicacionesList.innerHTML = '';

        // Iterar sobre cada publicación y crear tarjetas (cards) para mostrarlas
        publicaciones.forEach(publicacion => {
            const fecha = new Date(publicacion.fechaPublicacion).toLocaleString('es-ES');

            // Crear elementos de tarjeta (card) con Bootstrap
            const card = document.createElement('div');
            card.classList.add('card', 'col-10', 'mx-auto', 'mb-3'); // Añade clases de Bootstrap para diseño responsivo
            card.innerHTML = `
                <img src="/img/card_icon.png" class="card-img-top" alt="Icono de Tarjeta">
                <div class="card-body">
                    <h5 class="card-title">${publicacion.contenido}</h5>
                    <p class="card-text">Fecha de publicación: ${fecha}</p>
                    <ul class="list-group list-group-flush">
                        ${publicacion.comentarios.length > 0 ?
                            publicacion.comentarios.map(comment => `<li class="list-group-item">${comment.contenido}</li>`).join('') :
                            '<li class="list-group-item">Sin comentarios</li>'
                        }
                    </ul>
                    <p class="card-text">
                        Tags:
                        ${publicacion.tags.length > 0 ?
                            publicacion.tags.map((tag, index) => `<span>#${index + 1} ${tag}</span>`).join('') :
                            'No hay tags'
                        }
                    </p>
                    <button class="btn btn-primary" onclick="comentar(${publicacion.id})">Comentar</button>
                    <button class="btn btn-success" onclick="reaccionar(${publicacion.id}, 1)">Me gusta</button>
                    <button class="btn btn-info" onclick="reaccionar(${publicacion.id}, 2)">Me encanta</button>
                </div>
            `;
            publicacionesList.appendChild(card);
        });

    } catch (error) {
        console.error('Error:', error.message);
    }
}

// Llamar a la función cargarPublicaciones al cargar el DOM
document.addEventListener('DOMContentLoaded', cargarPublicaciones);

// Listener para redimensionar las imágenes cuando cambie el tamaño de la ventana
window.addEventListener('resize', cargarPublicaciones);

/**
 * Función para agregar un comentario a una publicación específica
 */
async function comentar(publicacionId) {
    const usuario = usuarioInfo.split('@')[0];
    try {
        const comentario = prompt('Escribe tu comentario:');
        if (!comentario) return; // Si el usuario cancela, no hacer nada

        const response = await fetch(`/publicaciones/comentar/usuario/${publicacionId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                usuario: usuario, // Usuario 21000017 realiza el comentario
                contenido: comentario
            })
        });

        if (!response.ok) {
            throw new Error('Error al agregar comentario');
        }

        // Recargar las publicaciones después de agregar el comentario
        cargarPublicaciones();

    } catch (error) {
        console.error('Error:', error.message);
    }
}

/**
 * Función para reaccionar a una publicación (me gusta o me encanta)
 */
async function reaccionar(publicacionId, tipoReaccion) {
    const usuario = usuarioInfo.split('@')[0];
    try {
        const response = await fetch(`/publicaciones/reaccionar/usuario/${publicacionId}/${tipoReaccion}`, {
            method: 'POST'
        });

        if (!response.ok) {
            throw new Error('Error al reaccionar');
        }

        // Recargar las publicaciones después de reaccionar
        cargarPublicaciones();

    } catch (error) {
        console.error('Error:', error.message);
    }
}

async function obtenerEstadisticas() {
    try {
        const response = await fetch('/estadisticas/contar');
        if (!response.ok) {
            throw new Error('Error al obtener estadísticas');
        }
        const estadisticas = await response.json();

        // Mostrar estadísticas en una alerta SweetAlert
        Swal.fire({
            icon: 'info',
            title: 'Estadísticas',
            html: `
                <p>Total de Tags: ${estadisticas.totalTags}</p>
                <p>Total de Comentarios: ${estadisticas.totalComentarios}</p>
                <p>Total de Reacciones Me Encanta: ${estadisticas.totalReaccionesMeEncanta}</p>
                <p>Total de Reacciones Me Gusta: ${estadisticas.totalReaccionesMeGusta}</p>
            `
        });

    } catch (error) {
        console.error('Error:', error.message);
        Swal.fire({
            icon: 'error',
            title: 'Error',
            text: 'Hubo un error al obtener las estadísticas'
        });
    }
}

// Llamar a la función al cargar la página para mostrar las publicaciones
document.addEventListener('DOMContentLoaded', cargarPublicaciones);


async function crearPublicacion() {
    const usuarioKey = 'usuario';
    const usuarioInfo = sessionStorage.getItem(usuarioKey);

    if (!usuarioInfo) {
        console.error('No se encontró información de usuario en sessionStorage.');
        return;
    }

    const usuario = usuarioInfo.split('@')[0];

    try {
        const { value: contenido } = await Swal.fire({
            icon: 'info',
            title: 'Crear nueva publicación',
            html: '<input id="swal-input1" class="swal2-input" placeholder="Escribe el contenido de tu publicación">',
            focusConfirm: false,
            preConfirm: () => {
                return document.getElementById('swal-input1').value;
            }
        });

        if (!contenido) {
            // El usuario canceló la creación de la publicación
            return;
        }

        // Obtener las opciones de categorías (tags) en mayúsculas
        const tagsDisponibles = [
            "JAVA", "PHP", "PYTHON", "JAVASCRIPT", "HTML", "CSS",
            "PROGRAMMING", "DEVELOPMENT", "CODING", "ALGORITHMS",
            "DATABASES", "GIT", "VERSION CONTROL", "FRONTEND", "BACKEND",
            "WEB", "MOBILE", "FRAMEWORKS", "LINUX", "SECURITY"
        ];

        const options = tagsDisponibles.map(tag => `<option value="${tag}">${tag}</option>`).join('');

        const { value: selectedTags } = await Swal.fire({
            icon: 'info',
            title: 'Selecciona los tags para tu publicación',
            html: `<select id="swal-select" class="swal2-select" multiple>${options}</select>`,
            focusConfirm: false,
            preConfirm: () => {
                return Array.from(document.getElementById('swal-select').selectedOptions).map(option => option.value.toUpperCase());
            }
        });

        if (!selectedTags || selectedTags.length === 0) {
            // El usuario canceló la selección de tags
            return;
        }

        const url = `http://localhost:8081/publicaciones/${usuario}/postear`;

        const response = await fetch(url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                tags: selectedTags,
                contenido: contenido
            })
        });

        if (!response.ok) {
            throw new Error('Error al crear la publicación');
        }

        // Mostrar una alerta de SweetAlert2 para confirmar la creación exitosa de la publicación
        Swal.fire({
            icon: 'success',
            title: 'Publicación creada',
            text: 'La publicación ha sido creada exitosamente.',
            confirmButtonText: 'OK'
        });

        // Recargar las publicaciones después de crear la nueva publicación
        cargarPublicaciones();

    } catch (error) {
        console.error('Error:', error.message);

        // Mostrar una alerta de SweetAlert2 para informar sobre el error al crear la publicación
        Swal.fire({
            icon: 'error',
            title: 'Error al crear la publicación',
            text: 'Hubo un error al intentar crear la publicación.',
            confirmButtonText: 'OK'
        });
    }
}

function limpiarSessionStorage() {
    // Limpiar todas las claves de sessionStorage
    sessionStorage.clear();
    // Redirigir a la página de inicio (por ejemplo)
    window.location.href = '/';
}

function setUsername() {
    const usuarioKey = 'usuario';
    const usuarioInfo = sessionStorage.getItem(usuarioKey);

    if (usuarioInfo) {
        const username = usuarioInfo.split('@')[0];
        console.log(username); // Mostrar el nombre de usuario en la consola (opcional)

        // Obtener el elemento span por su ID
        const txtUsernameElement = document.getElementById("txtUsername");

        // Verificar si se encontró el elemento
        if (txtUsernameElement) {
        // Mostrar una alerta de SweetAlert2 con el mensaje de bienvenida
                Swal.fire({
                    icon: 'success',
                    title: `¡Bienvenido, ${username}!`,
                    text: 'Estás conectado.',
                    confirmButtonText: 'OK'
                });
        // Establecer el texto dentro del elemento span
            txtUsernameElement.textContent = username;
        }
    }
}

document.addEventListener('DOMContentLoaded', setUsername);