const canvas = document.getElementById('graphCanvas');

const checkboxes = document.querySelectorAll(".checkR");
const resChecks = document.getElementById("resultCheckBox");
const dynamic_checked = document.querySelectorAll(".check_dynamic").item(0);

const scene = new THREE.Scene();
const camera = new THREE.PerspectiveCamera(75, canvas.width / canvas.height, 0.1, 1000);
const renderer = new THREE.WebGLRenderer({ canvas: canvas });
renderer.setSize(canvas.width, canvas.height);

// Установка однотонного фона (например, темно-серого цвета)
scene.background = new THREE.Color(0x333333);

// Управление камерой
const controls = new THREE.OrbitControls(camera, renderer.domElement);
controls.enableDamping = true;
controls.dampingFactor = 0.05;



// Настройка камеры
camera.position.set(4, 4, 4);
camera.lookAt(0, 0, 0);

animate();

function decimalWithComplement(code) {
    let num = parseInt(code, 2);
    if (num >= 2 ** (code.length - 1)) {
        num = num - 2 ** code.length;
    }
    return num;
}


checkboxes.forEach(checkbox => {
    checkbox.addEventListener('change', function() {
        let res = ""
        checkboxes.forEach(cb => {
            if (cb.checked === true) {
                res += "1"
            }
            else {
                res += "0"
            }
        });
        console.log("BIN: " + res)
        let result = parseInt(decimalWithComplement(res))
        console.log("DEC: " + result)
        if (result <= 5 && result >= -5) {
            resChecks.textContent = result.toString()
            document.getElementById("myform:r").value = result;
            drawGraphic(result)
            console.log("OK")
        }
        else {
            this.checked = !this.checked;
            console.log("BAD")
        }
    });
});


dynamic_checked.addEventListener('change', function (){
    checkboxes.forEach(checkbox => {
        checkbox.dispatchEvent(new Event('change'));
    });
})

window.addEventListener('load', () => {
    checkboxes.forEach(checkbox => {
        checkbox.dispatchEvent(new Event('change'));
    });
});

function drawGraphic(r){

    scene.clear()
    // Создание осей
    const axisX = createAxis(0xf0f0f0, new THREE.Vector3(-5, 0, 0), new THREE.Vector3(5, 0, 0)); // Красная ось X
    const axisY = createAxis(0xf0f0f0, new THREE.Vector3(0, -5, 0), new THREE.Vector3(0, 5, 0)); // Зеленая ось Y
    const axisZ = createAxis(0xf0f0f0, new THREE.Vector3(0, 0, -5), new THREE.Vector3(0, 0, 5)); // Синяя ось Z

    // Добавление осей на сцену
    scene.add(axisX);
    scene.add(axisY);
    scene.add(axisZ);

    // Параметры сетки
    const size = 5.5; // Границы поиска (от -size до size)
    const step = 0.07; // Шаг поиска
    const threshold = 0.3; // Точность попадания

    // Уравнение поверхности
    const implicitFunction = (x, y, z) => {
        return x ** 2 + y ** 2 + z ** 2 + Math.sin(4 * x) + Math.sin(4 * y) + Math.sin(4 * z) - r;
    };

    // Создание точек поверхности
    const vertices = [];
    for (let x = -size; x <= size; x += step) {
        for (let y = -size; y <= size; y += step) {
            for (let z = -size; z <= size; z += step) {
                const value = implicitFunction(x, y, z);
                if (Math.abs(value) < threshold) {
                    vertices.push(new THREE.Vector3(x, y, z));
                }
            }
        }
    }

    // Генерация точек
    const geometry = new THREE.BufferGeometry().setFromPoints(vertices);
    const material = new THREE.PointsMaterial({ color: 0x003366, size: 0.05 });
    const points = new THREE.Points(geometry, material);
    scene.add(points);


    // Луч для обработки кликов
    const raycaster = new THREE.Raycaster();

    isDynamicChecked()

}

function isDynamicChecked(){
    if (dynamic_checked.checked) {
        points.forEach(point => {
            drawPoint(point.x, point.y, point.z,getColor(point.x, point.y, point.z, Number(resChecks.textContent)) ? "green" : "red");
        });
    }
    else{
        points.forEach(point => {
            drawPoint(point.x, point.y, point.z, point.color);
        });
    }
}


function getColor(x, y, z, r){
    return x**2+y**2+z**2+Math.sin(4*x)+Math.sin(4*y)+Math.sin(4*z)<=r;
}

// Функция для создания осей
function createAxis(color, start, end) {
    const geometry = new THREE.BufferGeometry().setFromPoints([start, end]);
    const material = new THREE.LineBasicMaterial({ color: color });
    const axis = new THREE.Line(geometry, material);
    return axis;
}
// Функция для добавления точки
function drawPoint(x, y, z, color) {
    const geometry = new THREE.SphereGeometry(0.05, 32, 32);
    const material = new THREE.MeshBasicMaterial({color: color});
    const point = new THREE.Mesh(geometry, material);
    point.position.set(x, y, z);
    scene.add(point);
}

function animate() {
    requestAnimationFrame(animate);
    controls.update(); // Обновление OrbitControls
    renderer.render(scene, camera);
}


