document.addEventListener('DOMContentLoaded', () => {
    const authSection = document.getElementById('auth-section');
    const gameSection = document.getElementById('game-section');
    const loginForm = document.getElementById('loginForm');
    const registerForm = document.getElementById('registerForm');
    const restablecerForm = document.getElementById('restablecerForm');
    const showLoginButton = document.getElementById('showLogin');
    const showLoginButton2 = document.getElementById('showLogin2');
    const showRegisterButton = document.getElementById('showRegister');
    const showRestablecerButton = document.getElementById('showRestablecer');
    const logoutButton = document.getElementById('logout-button');
    const authSectionTitle = document.getElementById('auth-section-title');
    const skinSelect = document.getElementById('skin');
    // Blackjack
    const pedirCarta = document.getElementById('pedir-carta');
    const plantarse = document.getElementById('plantarse');

    pedirCarta.addEventListener('click', () => {
        const id = localStorage.getItem('juegoID');
        fetch(`/pedir-carta/${id}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            }
        })
            .then(response => { return response.ok ? response.json() : Promise.reject(response) })
            .then(data => {
                //pasan cosas xd
            })
            .catch(error => error.text().then(message => {
                console.error(message);
            }));
    });

    plantarse.addEventListener('click', () => {
        const id = localStorage.getItem('juegoID');
        fetch(`/plantarse/${id}`, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('token')
            }
        })
            .then(response => { return response.ok ? response.json() : Promise.reject(response) })
            .then(data => {
                //pasan cosas xd
                finalizarJuego();
            })
            .catch(error => error.text().then(message => {
                console.error(message);
            }));
    });
    // Fin Blackjack

    const toggleSections = () => {
        if (!!localStorage.getItem('token')) {
            authSection.style.display = 'none';
            gameSection.style.display = 'block';
        } else {
            gameSection.style.display = 'none';
            authSection.style.display = 'block';
        }
    };

    // Manejo del formulario de login
    loginForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('loginUsername').value;
        const password = document.getElementById('loginPassword').value;

        const response = await fetch('/api/auth/login', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password })
        });

        if (response.ok) {
            const token = await response.text(); // Podrías guardar un JWT si usas uno
            localStorage.setItem('token', token);
            loadPremios();
            loadCoins();
            cargarSkins();
            loadRanking();
            toggleSections();
        } else {
            alert('Usuario o contraseña incorrectos');
        }
    });

    // Manejo del formulario de registro
    registerForm.addEventListener('submit', async (e) => {
        e.preventDefault();
        const username = document.getElementById('registerUsername').value;
        const password = document.getElementById('registerPassword').value;
        const email = document.getElementById('registerEmail').value;

        const response = await fetch('/api/auth/register', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify({ username, password, email, rol: 'ROLE_USER' })
        });

        if (response.ok) {
            alert('Usuario registrado exitosamente');
            authSectionTitle.innerText = 'Iniciar Sesión';
            loginForm.style.display = 'block';
            registerForm.style.display = 'none';
        } else {
            const error = await response.text();
            alert(`Error: ${error}`);
        }
    });

    // Manejo del formulario de restablecer contraseña
    restablecerForm.addEventListener('submit', (e) => {
        e.preventDefault();
        const correo = document.getElementById('restablecerEmail').value.trim();
        const mensajeDiv = document.getElementById('restablecerMessage');

        if (correo !== '') {
            fetch('/api/auth/olvidar-contrasena', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify({ email: correo })
            })
                .then(response => response.text())
                .then(data => mensajeDiv.innerHTML = data)
                .catch(error => mensajeDiv.innerHTML = error);
        } else {
            mensajeDiv.innerHTML = 'Por favor, ingrese su correo electrónico.';
        }
    });

    // Para cambiar el title del select igual a su option
    skinSelect.addEventListener('change', () => {
        skinSelect.title = skinSelect.options[skinSelect.selectedIndex].title;
        localStorage.setItem("lastSkin", skinSelect.options[skinSelect.selectedIndex].value);
        loadPremios(skinSelect.selectedOptions[0].id);
    });

    // Alternar entre login y registro
    showLoginButton.addEventListener('click', () => {
        registerForm.style.display = 'none';
        authSectionTitle.innerText = 'Iniciar Sesión';
        loginForm.style.display = 'block';
    });

    showLoginButton2.addEventListener('click', () => {
        restablecerForm.style.display = 'none';
        authSectionTitle.innerText = 'Iniciar Sesión';
        loginForm.style.display = 'block';
    });

    showRegisterButton.addEventListener('click', () => {
        loginForm.style.display = 'none';
        authSectionTitle.innerText = 'Registrarse';
        registerForm.style.display = 'block';
    });

    showRestablecerButton.addEventListener('click', () => {
        loginForm.style.display = 'none';
        authSectionTitle.innerText = 'Restablecer Contraseña';
        restablecerForm.style.display = 'block';
    });

    // Manejo del cierre de sesión
    logoutButton.addEventListener('click', () => {
        localStorage.removeItem('token');
        toggleSections();
    });

    // Manejo para cargar las skins en el select
    const cargarSkins = () => {
        fetch('/skins/desbloqueadas', {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
                'Authorization': 'Bearer ' + localStorage.getItem('token')  // Aquí pasas el token
            }
        })
            .then(response => {
                if (!response.ok) {
                    throw new Error('No se pudo cargar las skins');
                }
                return response.json();
            })
            .then(data => {
                // Limpia las opciones existentes
                skinSelect.innerHTML = '';

                // Agrega las skins desbloqueadas como opciones
                data.reverse().forEach(skin => {
                    const option = document.createElement('option');
                    option.value = skin.name;
                    option.id = skin.id;
                    option.textContent = skin.name;
                    option.title = skin.description;
                    option.selected = skin.name == localStorage.getItem("lastSkin") ? true : false;
                    skinSelect.appendChild(option);
                });

                // Actualizamos el title del select
                skinSelect.title = skinSelect.options[skinSelect.selectedIndex].title;

                // Carga inicial del panel de premios
                loadPremios(skinSelect.selectedOptions[0].id);
            })
            .catch(error => {
                console.error('Error:', error);
            });
    };

    // Comprueba si hay token almacenado
    if (checkAuth()) {
        loadPremios();
        loadCoins();
        cargarSkins();
        loadRanking();
    } else {
        localStorage.removeItem('token');
    }

    // Inicializa el estado inicial
    toggleSections();
});

// Función para cargar las monedas del usuario
function loadCoins() {
    const coinsAmount = document.getElementById('coins-amount');
    fetch('/coins', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
    })
        .then(response => response.json())
        .then(data => {
            coinsAmount.textContent = data + "🪙";
        })
        .catch(error => console.error('Error:', error));
};

// Función para empezar el juego
function playGame(apuesta) {
    // Obtén los botones de apuestas y las secciones de juego y apuestas
    const buttons = document.querySelectorAll('.play-button');
    const apuestas = document.querySelector('.apuestas');
    const juego = document.querySelector('.juego');

    // Añade la clase disabled a todos los botones de apuestas
    buttons.forEach(button => button.classList.add('disabled'));

    // Envía la solicitud al servidor
    fetch(`/crear-juego/${apuesta}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
    })
        .then(response => { return response.ok ? response.json() : Promise.reject(response) })
        .then(data => {
            // Se habilita la zona de juego y guardamos su id
            apuestas.style.display = 'none';
            juego.style.display = 'block';
            console.log(data);
            localStorage.setItem('juegoID', data.id);
            loadCoins();
        })
        .catch(error => error.text().then(message => {
            console.error(message);
            // Quita la clase disabled a todos los botones
            buttons.forEach(button => button.classList.remove('disabled'));
        }));
}

function finalizarJuego() {
    localStorage.removeItem('juegoID');
    const juego = document.querySelector('.juego');
    const apuestas = document.querySelector('.apuestas');
    const buttons = document.querySelectorAll('.play-button');
    juego.style.display = 'none';
    apuestas.style.display = 'block';
    buttons.forEach(button => button.classList.remove('disabled'));
    loadCoins();
};

function actualizarJuego(juego) {
    const manoJugador = document.getElementById('mano-jugador');
    const valorJugador = document.getElementById('valor-jugador');
    const manoIA = document.getElementById('mano-ia');
    const valorIA = document.getElementById('valor-ia');
    const resultado = document.getElementById('resultado');

    manoJugador.textContent = juego.manoJugador.map(carta => {
        let palo;
        let valor;
        switch (carta.palo) {
            case 'CORAZONES':
                palo = '♥️';
                break;
            case 'DIAMANTES':
                palo = '♦️';
                break;
            case 'PICAS':
                palo = '♠️';
                break;
            case 'TREBOLES':
                palo = '♣️';
                break;
            default:
                palo = '';
                break;
        }
        switch (carta.valor) {
            case value:
                valor = 11;
                break;

            default:
                valor = 0;
                break;
        }
    }).join(',');
    valorJugador.textContent = "";//me he  quedado por aqui
}

// Agrega eventos de clic a los botones de apuestas
document.getElementById('apuesta-5').addEventListener('click', function () {
    if (!this.classList.contains('disabled')) {
        playGame(5);
    }
});

document.getElementById('apuesta-25').addEventListener('click', function () {
    if (!this.classList.contains('disabled')) {
        playGame(25);
    }
});

document.getElementById('apuesta-50').addEventListener('click', function () {
    if (!this.classList.contains('disabled')) {
        playGame(50);
    }
});

document.getElementById('show-wins-button').addEventListener('click', function () {
    fetch('/wins', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
    })
        .then(response => response.json())
        .then(data => {
            document.getElementById('message').textContent = "Victorias: " + data;
        })
        .catch(error => console.error('Error:', error));
});

document.getElementById('tienda').addEventListener('click', function () {
    location.href = "/tienda";
});

function loadRanking() {
    fetch('/ranking', {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
            'Authorization': 'Bearer ' + localStorage.getItem('token')
        }
    })
        .then(response => response.json())
        .then(data => {
            const rankingPanel = document.getElementById('ranking-panel');
            const rankingTitle = document.createElement('h3');
            const hr = document.createElement('hr');
            const rankingTable = document.createElement('table');
            const rankingTableGroup = document.createElement('colgroup');
            const colNombre = document.createElement('col');
            const colVictorias = document.createElement('col');

            rankingTitle.textContent = "RANKING";
            colNombre.span = "1";
            colNombre.style = "width: 75%";
            colVictorias.span = "1";
            colVictorias.style = "width: 25%";

            rankingTableGroup.appendChild(colNombre);
            rankingTableGroup.appendChild(colVictorias);
            rankingTable.appendChild(rankingTableGroup);

            data.usuarios.forEach((usuario, i) => {
                const fila = document.createElement('tr');
                const nombre = document.createElement('td');
                const victorias = document.createElement('td');

                nombre.textContent = usuario.username;
                nombre.title = usuario.username;
                victorias.textContent = data.victorias[i];
                victorias.title = data.victorias[i] + " victorias";

                fila.appendChild(nombre);
                fila.appendChild(victorias);
                rankingTable.appendChild(fila);
            });

            rankingPanel.replaceChildren(rankingTitle, hr, rankingTable);
        })
        .catch(error => console.error('Error:', error));
};

function loadPremios() {
    const premiosPanel = document.getElementById('premios-panel');
    const premiosTitle = document.createElement('h3');
    const hr = document.createElement('hr');
    const premiosTable = document.createElement('table');
    const premiosTableGroup = document.createElement('colgroup');
    const colApuestas = document.createElement('col');
    const colResultados = document.createElement('col');
    const colPremios = document.createElement('col');
    const apuestas = [5, 25, 50];

    premiosTitle.textContent = "PREMIOS";
    colApuestas.span = "1";
    colApuestas.style = "width: 30%";
    colResultados.span = "1";
    colResultados.style = "width: 40%";
    colPremios.span = "1";
    colPremios.style = "width: 30%";

    premiosTableGroup.appendChild(colApuestas);
    premiosTableGroup.appendChild(colResultados);
    premiosTableGroup.appendChild(colPremios);
    premiosTable.appendChild(premiosTableGroup);

    apuestas.forEach((apuesta) => {
        let resultado;
        let premio;
        for (let i = 0; i < 3; i++) {
            const fila = document.createElement('tr');
            const colApuesta = document.createElement('td');
            const colResultado = document.createElement('td');
            const colPremio = document.createElement('td');

            switch (i) {
                case 0:
                    resultado = "Victoria";
                    premio = apuesta * 2;
                    break;

                case 1:
                    resultado = "Empate";
                    premio = apuesta;
                    break;

                case 2:
                    resultado = "Derrota";
                    premio = 0;
                    break;
            }

            colApuesta.textContent = `${apuesta}\t🪙`;
            colApuesta.title = `${apuesta} 🪙`;
            colResultado.textContent = resultado;
            colResultado.title = resultado;
            colPremio.textContent = `${premio}\t🪙`;
            colPremio.title = `${premio} 🪙`;

            fila.appendChild(colApuesta);
            fila.appendChild(colResultado);
            fila.appendChild(colPremio);
            premiosTable.appendChild(fila);
        }
    });
    premiosPanel.replaceChildren(premiosTitle, hr, premiosTable);
}

function checkAuth() {
    try {
        const token = JSON.parse(atob(localStorage.getItem('token').split('.')[1]));
        return token.exp > Math.floor(Date.now() / 1000);
    } catch (error) {
        return false;
    }
}

function pagarCoins(cost) {
    const coinsAmount = document.getElementById('coins-amount');
    const dinero = parseInt(coinsAmount.textContent.slice(0, -2), 10);
    coinsAmount.textContent = (dinero - cost) + "🪙";
}