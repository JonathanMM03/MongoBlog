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
            card.classList.add('col-lg-8', 'col-md-10', 'col-sm-11', 'mx-auto', 'mb-3'); // Añade 'mx-auto' para centrar la card
            card.innerHTML = `
                <div class="card">
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
                </div>
            `;
            publicacionesList.appendChild(card);
        });

    } catch (error) {
        console.error('Error:', error.message);
    }
}

/**
 * Función para agregar un comentario a una publicación específica
 */
async function comentar(publicacionId) {
    try {
        const comentario = prompt('Escribe tu comentario:');
        if (!comentario) return; // Si el usuario cancela, no hacer nada

        const response = await fetch(`/publicaciones/comentar/21000017/${publicacionId}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify({
                usuario: '21000017', // Usuario 21000017 realiza el comentario
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
    try {
        const response = await fetch(`/publicaciones/reaccionar/21000017/${publicacionId}/${tipoReaccion}`, {
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