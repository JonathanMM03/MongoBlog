document.addEventListener('DOMContentLoaded', function() {
    const signUpButton = document.getElementById('signUp');
    const signInButton = document.getElementById('signIn');
    const container = document.getElementById('container');

    function showRegisterMenu() {
        container.classList.add("right-panel-active");
    }

    // Función para mostrar el menú de inicio de sesión
    function showLoginMenu() {
        container.classList.remove("right-panel-active");
    }

    // Asignar eventos a los botones
    signUpButton.onclick = showRegisterMenu;
    signInButton.onclick = showLoginMenu;

    const emailInput = document.getElementById('email');
        emailInput.addEventListener('input', function(event) {
            const emailValue = event.target.value;
            if (emailValue.trim() !== '') {
                // Almacena el correo electrónico en sessionStorage
                sessionStorage.setItem('usuario', emailValue);
            }
        });

});

/*document.addEventListener('DOMContentLoaded', function() {
    const signInForm = document.getElementById('signInForm');

    signInForm.addEventListener('submit', function(event) {
        event.preventDefault(); // Evitar el envío del formulario por defecto

        // Recoger los valores del formulario
        const email = document.getElementById('email').value;
        const password = document.getElementById('password').value;

        // Construir el objeto de datos para enviar al servidor
        const formData = new FormData();
        formData.append('email', email);
        formData.append('password', password);

        // Realizar la llamada AJAX al servidor para iniciar sesión
        fetch('/login', {
            method: 'POST',
            body: formData
        })
        .then(response => {
            if (response.ok) {
                // La autenticación fue exitosa, redirigir a la página principal
                window.location.replace('/home'); // Redirige a la página de inicio después del inicio de sesión exitoso
            } else {
                // La autenticación falló, mostrar mensaje de error o tomar medidas apropiadas
                throw new Error('Error en la autenticación');
            }
        })
        .catch(error => {
            console.error('Error:', error);
        });
    });
});*/

document.addEventListener('DOMContentLoaded', function() {
    const registerForm = document.getElementById('registerForm');
        registerForm.addEventListener('submit', function(event) {
            event.preventDefault(); // Evitar el envío del formulario por defecto

            const nombre = document.getElementById('nombre').value;
            const apellidos = document.getElementById('apellidos').value;
            const fechaNacimientoInput = document.getElementById('fechaNacimiento').value;
            const email = document.getElementById('email').value;
            const password = document.getElementById('password').value;

            const fechaNacimiento = formatDateToISODate(fechaNacimientoInput);

            if (fechaNacimiento) {
                const userData = {
                    nombre: nombre,
                    apellidos: apellidos,
                    fechaNacimiento: fechaNacimiento,
                    email: email,
                    password: password
                };

                fetch("http://localhost:8081/usuario/", {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json'
                    },
                    body: JSON.stringify(userData)
                })
                .then(response => {
                    if (response.ok) {
                        return response.json();
                    } else {
                        throw new Error('Error en el registro');
                    }
                })
                .then(data => {
                    // Registro exitoso, almacenar datos adicionales en sessionStorage
                    sessionStorage.setItem('emailLogin', email); // Guardar email como usuario de sesión
                    sessionStorage.setItem('passwordLogin', password); // Guardar contraseña de sesión
                    console.log('Usuario registrado:', data);
                    window.location.replace('/home'); // Redirigir a la página de inicio después del registro
                })
                .catch(error => {
                    console.error('Error:', error);
                    // Manejar errores de registro aquí
                });
            } else {
                console.error('Formato de fecha incorrecto. Debe ser YYYY-mm-dd.');
                // Manejar errores de formato de fecha aquí
            }
        });

    // Función para convertir la fecha a formato ISO 8601 (YYYY-mm-dd)
    function formatDateToISODate(dateString) {
        const dateObject = new Date(dateString);
        if (isNaN(dateObject.getTime())) {
            return null; // La fecha no es válida
        }
        const isoDateString = dateObject.toISOString().split("T")[0];
        return isoDateString;
    }
});

// Función para registrar el usuario
function registrar(evento) {
    evento.preventDefault(); // Evitar el comportamiento predeterminado del botón

    // Recoger los valores del formulario
    const nombre = document.getElementById('nombre').value;
    const apellidos = document.getElementById('apellidos').value;
    const fechaNacimientoInput = document.getElementById('fechaNacimiento').value;
    const email = document.getElementById('email').value;
    const password = document.getElementById('password').value;

    // Convertir la fecha de nacimiento a formato YYYY-mm-dd
    const fechaNacimiento = formatDateToISODate(fechaNacimientoInput);

    // Validar el formato de la fecha de nacimiento
    if (fechaNacimiento) {
        // Construir el objeto de datos en formato JSON
        const userData = {
            nombre: nombre,
            apellidos: apellidos,
            fechaNacimiento: fechaNacimiento,
            email: email,
            password: password
        };

        console.table(userData);
        console.log(JSON.stringify(userData));

        // Realizar la llamada AJAX al servidor para registrar el usuario
        fetch("http://localhost:8081/usuario/", {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json'
            },
            body: JSON.stringify(userData) // Convertir objeto a JSON
        })
        .then(response => {
            if (response.ok) {
                // El registro fue exitoso
                console.log(response);
                return response.json();
            } else {
                // El registro falló, mostrar mensaje de error o tomar medidas apropiadas
                throw new Error('Error en el registro');
            }
        })
        .then(data => {
            // El usuario se registró correctamente
            console.log('Usuario registrado:', data);
        })
        .catch(error => {
            console.error('Error:', error);
            // Aquí puedes manejar el error del registro, como mostrar un mensaje de error al usuario
        });
    } else {
        console.error('Formato de fecha incorrecto. Debe ser YYYY-mm-dd.');
        // Aquí puedes mostrar un mensaje de error al usuario o tomar otras acciones apropiadas
    }
}

// Función para convertir la fecha a formato ISO 8601 (YYYY-mm-dd)
function formatDateToISODate(dateString) {
    const dateObject = new Date(dateString);
    if (isNaN(dateObject.getTime())) {
        return null; // La fecha no es válida
    }
    const isoDateString = dateObject.toISOString().split("T")[0];
    return isoDateString;
}