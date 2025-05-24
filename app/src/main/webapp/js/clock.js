const canvas = document.getElementById('clock');
const ctx = canvas.getContext('2d');
const radius = canvas.width / 2;

ctx.translate(radius, radius);

const clockRadius = radius * 0.9;

function drawClock() {

    ctx.clearRect(-canvas.width, -canvas.height, 2 * canvas.width, 2 * canvas.height);

    drawFace(ctx, clockRadius);
    drawNumbers(ctx, clockRadius);
    drawTime(ctx, clockRadius);
}

function drawFace(ctx, radius) {

    ctx.beginPath();
    ctx.arc(0, 0, radius, 0, 2 * Math.PI);
    ctx.strokeStyle = 'white';
    ctx.lineWidth = 8;
    ctx.stroke();

    ctx.beginPath();
    ctx.arc(0, 0, 3, 0, 2 * Math.PI);
    ctx.fillStyle = 'white';
    ctx.fill();
}

function drawNumbers(ctx, radius) {
    ctx.font = `${radius * 0.15}px Arial`;
    ctx.textBaseline = 'middle';
    ctx.textAlign = 'center';
    ctx.fillStyle = 'white';
    for (let num = 1; num <= 12; num++) {
        const ang = (num * Math.PI) / 6;
        const x = radius * 0.85 * Math.cos(ang - Math.PI / 2);
        const y = radius * 0.85 * Math.sin(ang - Math.PI / 2);
        ctx.fillText(num.toString(), x, y);
    }
    ctx.fillText("ИBT", 0, radius * 0.35);
}

function drawTime(ctx, radius) {
    const now = new Date();
    const hour = now.getHours();
    const minute = now.getMinutes();
    const second = now.getSeconds();

    const hourAngle = ((hour % 12) * 2 * Math.PI) / 12 + (minute * 2 *Math.PI) / 720;
    drawArrow(ctx, hourAngle, radius * 0.5, radius * 0.05);

    const minuteAngle = (minute * 2 * Math.PI) / 60 + (second * 2 * Math.PI) / 3600;
    drawArrow(ctx, minuteAngle, radius * 0.7, radius * 0.04);

    const secondAngle = (second * 2 * Math.PI) / 60;
    drawArrow(ctx, secondAngle, radius * 0.85, radius * 0.02, '#663256');
}

function drawArrow(ctx, pos, length, width, color = '#003366') {
    ctx.beginPath();
    ctx.lineWidth = width;
    ctx.lineCap = 'round';
    ctx.strokeStyle = color;
    ctx.moveTo(0, 0);
    ctx.rotate(pos);
    ctx.lineTo(0, -length);
    ctx.stroke();
    ctx.rotate(-pos);
}

setInterval(drawClock, 1000);
drawClock();

const data = document.getElementById("date")
const real_date = (new Date()).toLocaleDateString('ru-RU', {
    day: '2-digit',
    month: 'long',
    year: 'numeric',
});
data.textContent = real_date



