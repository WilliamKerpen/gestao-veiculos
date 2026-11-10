const configuredApiUrl = document.querySelector('meta[name="api-base-url"]')?.content;
const API_BASE_URL = configuredApiUrl
    ? configuredApiUrl.replace(/\/+$/, '')
    : window.location.protocol === 'file:'
        ? 'http://localhost:8080'
        : window.location.port && window.location.port !== '8080'
            ? `${window.location.protocol}//${window.location.hostname}:8080`
            : window.location.origin;

const api = {
    async request(path, options = {}) {
        const url = new URL(path, `${API_BASE_URL}/`);
        let response;
        try {
            response = await fetch(url, {
                ...options,
                headers: {
                    ...(options.body ? { 'Content-Type': 'application/json' } : {}),
                    ...options.headers
                }
            });
        } catch {
            throw new Error(`Não foi possível conectar à API em ${url.origin}. Verifique se a aplicação está ativa na porta 8080.`);
        }
        if (!response.ok) {
            let message = `A API respondeu ${response.status} para ${url.pathname}.`;
            try {
                const body = await response.json();
                message = `${body.erro || body.message || message} (${response.status} em ${url.pathname})`;
            } catch {
                // Mantém a mensagem HTTP quando a resposta não contém JSON.
            }
            throw new Error(message);
        }
        return response.status === 204 ? null : response.json();
    }
};

const state = {
    vehicles: [],
    allVehicles: [],
    brands: [],
    photos: [],
    currentPhotoIndex: null,
    photoVehicleId: null,
    statusVehicleId: null
};

const $ = (selector) => document.querySelector(selector);
const money = (value) => Number(value || 0).toLocaleString('pt-BR', { style: 'currency', currency: 'BRL' });
const number = (value) => Number(value || 0).toLocaleString('pt-BR');
const escapeHtml = (value) => String(value ?? '').replace(/[&<>"']/g, (character) => ({
    '&': '&amp;', '<': '&lt;', '>': '&gt;', '"': '&quot;', "'": '&#39;'
}[character]));

function showNotice(message, type = 'success') {
    const notice = $('#notice');
    notice.textContent = message;
    notice.className = `notice ${type}`;
    notice.hidden = false;
    window.clearTimeout(showNotice.timer);
    showNotice.timer = window.setTimeout(() => { notice.hidden = true; }, 5000);
}

function showError(element, error) {
    element.textContent = error.message || 'Ocorreu um erro.';
    element.hidden = false;
}

function brandName(id) {
    return state.brands.find((brand) => brand.id === id)?.nome || 'Marca não identificada';
}

function statusName(status) {
    return ({ DISPONIVEL: 'Disponível', RESERVADO: 'Reservado', VENDIDO: 'Vendido' })[status] || status;
}

async function loadDashboard() {
    try {
        const [vehicles, brands, stockValue] = await Promise.all([
            api.request('/api/veiculos'),
            api.request('/api/marcas'),
            api.request('/api/veiculos/estoque/valor')
        ]);
        state.allVehicles = vehicles;
        state.brands = brands;
        $('#total-count').textContent = number(vehicles.length);
        $('#available-count').textContent = number(vehicles.filter((vehicle) => vehicle.status === 'DISPONIVEL').length);
        $('#stock-value').textContent = money(stockValue);
        renderBrandOptions();
        renderBrands();
        await loadFilteredVehicles();
    } catch (error) {
        showNotice(error.message, 'error');
        $('#vehicle-grid').innerHTML = `<div class="empty-state"><strong>Não foi possível carregar o estoque</strong><p>${escapeHtml(error.message)}</p></div>`;
    }
}

function filterQuery() {
    const params = new URLSearchParams();
    for (const [key, value] of new FormData($('#filters-form')).entries()) {
        if (String(value).trim()) params.set(key, String(value).trim());
    }
    return params.toString();
}

async function loadFilteredVehicles() {
    const grid = $('#vehicle-grid');
    grid.innerHTML = '<div class="loading-state"><span class="spinner"></span> Consultando o estoque...</div>';
    try {
        const query = filterQuery();
        const vehicles = await api.request(`/api/veiculos${query ? `?${query}` : ''}`);
        state.vehicles = vehicles;
        $('#result-count').textContent = `${vehicles.length} ${vehicles.length === 1 ? 'veículo' : 'veículos'}`;
        renderVehicles();
    } catch (error) {
        showNotice(error.message, 'error');
        grid.innerHTML = `<div class="empty-state"><strong>Não foi possível buscar os veículos</strong><p>${escapeHtml(error.message)}</p></div>`;
    }
}

function renderVehicles() {
    const grid = $('#vehicle-grid');
    if (!state.vehicles.length) {
        grid.innerHTML = '<div class="empty-state"><strong>Nenhum veículo encontrado</strong><p>Tente ajustar ou limpar os filtros para ver mais resultados.</p></div>';
        return;
    }
    grid.innerHTML = state.vehicles.map((vehicle) => {
        const photos = (vehicle.fotos || '').split(/\r?\n/).map((url) => url.trim()).filter(Boolean);
        const photo = photos[0];
        return `
            <article class="vehicle-card">
                <div class="vehicle-media" data-carousel-index="0">
                    ${photo ? `<img class="vehicle-photo" src="${escapeHtml(photo)}" alt="${escapeHtml(`${brandName(vehicle.marcaId)} ${vehicle.modelo}`)}" loading="lazy" referrerpolicy="no-referrer" data-photos="${escapeHtml(JSON.stringify(photos))}">` : ''}
                    <div class="photo-placeholder" ${photo ? 'hidden' : ''}><span class="placeholder-icon">⌑</span><span>${photo ? 'IMAGEM INDISPONÍVEL' : 'IMAGEM NÃO CADASTRADA'}</span></div>
                    ${photos.length > 1 ? `
                        <button class="carousel-arrow carousel-previous" type="button" data-carousel-action="previous" aria-label="Foto anterior">‹</button>
                        <button class="carousel-arrow carousel-next" type="button" data-carousel-action="next" aria-label="Próxima foto">›</button>
                        <div class="carousel-indicators" aria-label="Selecionar foto">
                            ${photos.map((_, index) => `<button class="carousel-indicator${index === 0 ? ' active' : ''}" type="button" data-carousel-action="go" data-index="${index}" aria-label="Mostrar foto ${index + 1}" aria-current="${index === 0 ? 'true' : 'false'}"></button>`).join('')}
                        </div>
                        <span class="carousel-counter" aria-live="polite">1 / ${photos.length}</span>
                    ` : ''}
                    <span class="status status-${escapeHtml(vehicle.status)} media-status">${statusName(vehicle.status)}</span>
                    <button class="photo-count text-button" type="button" data-action="photos" data-id="${vehicle.id}">${photos.length ? `▧ ${photos.length} ${photos.length === 1 ? 'foto' : 'fotos'}` : '+ adicionar foto'}</button>
                </div>
                <div class="vehicle-content">
                    <p class="vehicle-brand">${escapeHtml(brandName(vehicle.marcaId))}</p>
                    <h3 class="vehicle-title" title="${escapeHtml(vehicle.modelo)}">${escapeHtml(vehicle.modelo)}</h3>
                    <p class="vehicle-version">${escapeHtml(vehicle.placa)} · ${escapeHtml(vehicle.cor || 'Cor não informada')}</p>
                    <div class="vehicle-specs">
                        <span><i class="spec-dot">◆</i> ${escapeHtml(vehicle.ano)}</span>
                        <span><i class="spec-dot">↗</i> ${number(vehicle.quilometragem)} km</span>
                        <span><i class="spec-dot">◈</i> ${escapeHtml(vehicle.cambio || '—')}</span>
                        <span><i class="spec-dot">◉</i> ${escapeHtml(vehicle.combustivel || '—')}</span>
                    </div>
                    <div class="vehicle-bottom">
                        <div><span class="price-caption">PREÇO DE VENDA</span><strong class="vehicle-price">${money(vehicle.precoVenda)}</strong></div>
                        <div class="card-actions">
                            <button class="text-button" type="button" data-action="edit" data-id="${vehicle.id}">Editar</button>
                            ${vehicle.status !== 'VENDIDO' ? `<button class="text-button" type="button" data-action="status" data-id="${vehicle.id}">Status</button><button class="text-button danger" type="button" data-action="delete" data-id="${vehicle.id}">Remover</button>` : '<span class="text-button" title="Registro mantido para relatórios">Histórico</span>'}
                        </div>
                    </div>
                </div>
            </article>`;
    }).join('');

    grid.querySelectorAll('.vehicle-photo').forEach((image) => {
        image.addEventListener('error', () => {
            image.hidden = true;
            const placeholder = image.parentElement.querySelector('.photo-placeholder');
            placeholder.hidden = false;
            placeholder.lastElementChild.textContent = 'IMAGEM INDISPONÍVEL';
        }, { once: true });
    });
}

function handleCarouselAction(event) {
    const button = event.target.closest('[data-carousel-action]');
    if (!button) return;

    const media = button.closest('.vehicle-media');
    const image = media.querySelector('.vehicle-photo');
    if (!image) return;

    const photos = JSON.parse(image.dataset.photos);
    const currentIndex = Number(media.dataset.carouselIndex || 0);
    let nextIndex;
    if (button.dataset.carouselAction === 'go') {
        nextIndex = Number(button.dataset.index);
    } else {
        const direction = button.dataset.carouselAction === 'next' ? 1 : -1;
        nextIndex = (currentIndex + direction + photos.length) % photos.length;
    }

    media.dataset.carouselIndex = String(nextIndex);
    image.src = photos[nextIndex];
    image.hidden = false;
    const placeholder = media.querySelector('.photo-placeholder');
    placeholder.hidden = true;
    placeholder.lastElementChild.textContent = 'IMAGEM NÃO CADASTRADA';
    media.querySelector('.carousel-counter').textContent = `${nextIndex + 1} / ${photos.length}`;
    media.querySelectorAll('.carousel-indicator').forEach((indicator, index) => {
        const active = index === nextIndex;
        indicator.classList.toggle('active', active);
        indicator.setAttribute('aria-current', String(active));
    });
}

function renderBrands() {
    $('#brand-list').innerHTML = state.brands.length
        ? state.brands.map((brand) => `<li>${escapeHtml(brand.nome)}</li>`).join('')
        : '<li>Nenhuma marca cadastrada.</li>';
}

function renderBrandOptions() {
    for (const selector of ['#filter-brand', '#marcaId']) {
        const select = $(selector);
        const selected = select.value;
        const placeholder = selector === '#filter-brand' ? 'Todas as marcas' : 'Selecione uma marca';
        select.innerHTML = `<option value="">${placeholder}</option>${state.brands.map((brand) =>
            `<option value="${brand.id}">${escapeHtml(brand.nome)}</option>`).join('')}`;
        if (state.brands.some((brand) => String(brand.id) === selected)) select.value = selected;
    }
}

function openVehicleForm(vehicle) {
    const form = $('#vehicle-form');
    form.reset();
    $('#vehicle-error').hidden = true;
    $('#vehicle-id').value = vehicle?.id || '';
    $('#vehicle-dialog-title').textContent = vehicle ? 'Editar veículo' : 'Adicionar veículo';
    renderBrandOptions();
    if (vehicle) {
        for (const field of ['marcaId', 'modelo', 'cor', 'ano', 'placa', 'chassi', 'quilometragem', 'combustivel', 'cambio', 'precoCompra', 'precoVenda']) {
            form.elements[field].value = vehicle[field] ?? '';
        }
    }
    $('#vehicle-photos').value = vehicle?.fotos || '';
    $('#vehicle-dialog').showModal();
}

async function saveVehicle(event) {
    event.preventDefault();
    const form = event.currentTarget;
    const id = $('#vehicle-id').value;
    const data = Object.fromEntries(new FormData(form).entries());
    for (const key of ['marcaId', 'ano', 'quilometragem']) data[key] = Number(data[key]);
    data.fotos = data.fotos.trim();
    try {
        await api.request(id ? `/api/veiculos/${id}` : '/api/veiculos', {
            method: id ? 'PUT' : 'POST',
            body: JSON.stringify(data)
        });
        $('#vehicle-dialog').close();
        await loadDashboard();
        showNotice(id ? 'Veículo atualizado com sucesso.' : 'Veículo adicionado ao estoque.');
    } catch (error) {
        showError($('#vehicle-error'), error);
    }
}

function findVehicle(id) {
    return state.allVehicles.find((vehicle) => String(vehicle.id) === String(id));
}

async function handleVehicleAction(event) {
    const button = event.target.closest('[data-action]');
    if (!button) return;
    const vehicle = findVehicle(button.dataset.id);
    if (!vehicle) return;
    try {
        if (button.dataset.action === 'edit') openVehicleForm(vehicle);
        if (button.dataset.action === 'photos') openPhotos(vehicle);
        if (button.dataset.action === 'status') openStatusForm(vehicle);
        if (button.dataset.action === 'delete' && window.confirm(`Remover ${vehicle.modelo} do estoque?`)) {
            await api.request(`/api/veiculos/${vehicle.id}`, { method: 'DELETE' });
            await loadDashboard();
            showNotice('Veículo removido do estoque.');
        }
    } catch (error) {
        showNotice(error.message, 'error');
    }
}

function openStatusForm(vehicle) {
    state.statusVehicleId = vehicle.id;
    $('#status-vehicle-name').textContent = `${vehicle.modelo} · ${vehicle.placa}`;
    $('#status-error').hidden = true;
    const options = vehicle.status === 'RESERVADO'
        ? [['DISPONIVEL', 'Disponível'], ['VENDIDO', 'Vendido']]
        : [['RESERVADO', 'Reservado'], ['VENDIDO', 'Vendido']];
    $('#new-status').innerHTML = options.map(([value, label]) =>
        `<option value="${value}">${label}</option>`).join('');
    $('#status-dialog').showModal();
}

async function saveStatus(event) {
    event.preventDefault();
    try {
        await api.request(`/api/veiculos/${state.statusVehicleId}/status`, {
            method: 'PATCH',
            body: JSON.stringify({ status: $('#new-status').value })
        });
        $('#status-dialog').close();
        await loadDashboard();
        showNotice('Status do veículo atualizado.');
    } catch (error) {
        showError($('#status-error'), error);
    }
}

function openPhotos(vehicle) {
    state.photoVehicleId = vehicle.id;
    state.currentPhotoIndex = null;
    state.photos = (vehicle.fotos || '').split(/\r?\n/).map((url) => url.trim()).filter(Boolean);
    $('#photos-title').textContent = `${vehicle.modelo} · fotos`;
    $('#photo-error').hidden = true;
    resetPhotoForm();
    $('#photos-dialog').showModal();
    renderPhotos();
}

function renderPhotos() {
    $('#photo-list').innerHTML = state.photos.length ? state.photos.map((url, index) => `
        <article class="photo-item">
            <img class="photo-thumb" src="${escapeHtml(url)}" alt="" loading="lazy" referrerpolicy="no-referrer">
            <a class="photo-url" href="${escapeHtml(url)}" target="_blank" rel="noopener noreferrer">${escapeHtml(url)}</a>
            <div class="photo-actions">
                <button class="text-button" type="button" data-photo-action="edit" data-index="${index}">Editar</button>
                <button class="text-button danger" type="button" data-photo-action="delete" data-index="${index}">Remover</button>
            </div>
        </article>`).join('') : '<p class="dialog-copy">Nenhum link de foto cadastrado.</p>';
    $('#photo-list').querySelectorAll('img').forEach((image) => {
        image.addEventListener('error', () => { image.hidden = true; }, { once: true });
    });
    const atLimit = state.photos.length >= 4 && state.currentPhotoIndex === null;
    $('#photo-submit').disabled = atLimit;
    $('#photo-url').disabled = atLimit;
    $('#photo-submit').textContent = atLimit ? 'Limite de 4 links' : (state.currentPhotoIndex !== null ? 'Salvar alteração' : 'Adicionar link');
}

function resetPhotoForm() {
    state.currentPhotoIndex = null;
    $('#photo-form').reset();
    $('#photo-error').hidden = true;
    $('#photo-url').disabled = false;
    $('#photo-cancel-edit').hidden = true;
    $('#photo-submit').textContent = 'Adicionar link';
    $('#photo-submit').disabled = state.photos.length >= 4;
}

function handlePhotoAction(event) {
    const button = event.target.closest('[data-photo-action]');
    if (!button) return;
    const index = Number(button.dataset.index);
    const url = state.photos[index];
    if (url === undefined) return;
    if (button.dataset.photoAction === 'edit') {
        state.currentPhotoIndex = index;
        $('#photo-url').value = url;
        $('#photo-cancel-edit').hidden = false;
        $('#photo-submit').textContent = 'Salvar alteração';
        $('#photo-submit').disabled = false;
        $('#photo-url').disabled = false;
        $('#photo-url').focus();
    } else if (window.confirm('Remover este link de foto?')) {
        const updatedPhotos = state.photos.filter((_, itemIndex) => itemIndex !== index);
        persistVehiclePhotos(updatedPhotos).catch((error) => showError($('#photo-error'), error));
    }
}

async function savePhoto(event) {
    event.preventDefault();
    const updatedPhotos = [...state.photos];
    const url = $('#photo-url').value.trim();
    if (state.currentPhotoIndex !== null) updatedPhotos[state.currentPhotoIndex] = url;
    else updatedPhotos.push(url);
    try {
        await persistVehiclePhotos(updatedPhotos);
        resetPhotoForm();
    } catch (error) {
        showError($('#photo-error'), error);
    }
}

async function persistVehiclePhotos(photos) {
    const vehicle = findVehicle(state.photoVehicleId);
    if (!vehicle) throw new Error('Veículo não encontrado para atualizar as fotos.');
    const data = Object.fromEntries([
        'marcaId', 'modelo', 'cor', 'ano', 'placa', 'chassi', 'quilometragem',
        'combustivel', 'cambio', 'precoCompra', 'precoVenda'
    ].map((field) => [field, vehicle[field]]));
    data.fotos = photos.join('\n');
    await api.request(`/api/veiculos/${vehicle.id}`, {
        method: 'PUT',
        body: JSON.stringify(data)
    });
    state.photos = photos;
    await loadDashboard();
    if ($('#photos-dialog').open) {
        renderPhotos();
        resetPhotoForm();
    }
}

async function saveBrand(event) {
    event.preventDefault();
    const input = $('#brand-name');
    try {
        await api.request('/api/marcas', {
            method: 'POST',
            body: JSON.stringify({ nome: input.value.trim() })
        });
        input.value = '';
        await loadDashboard();
        showNotice('Marca cadastrada com sucesso.');
    } catch (error) {
        showNotice(error.message, 'error');
    }
}

document.addEventListener('DOMContentLoaded', () => {
    $('#new-vehicle').addEventListener('click', () => openVehicleForm());
    $('#vehicle-form').addEventListener('submit', saveVehicle);
    $('#vehicle-grid').addEventListener('click', handleVehicleAction);
    $('#vehicle-grid').addEventListener('click', handleCarouselAction);
    $('#filters-form').addEventListener('submit', (event) => {
        event.preventDefault();
        loadFilteredVehicles();
    });
    $('#filters-form').addEventListener('reset', () => window.setTimeout(loadFilteredVehicles, 0));
    $('#brand-form').addEventListener('submit', saveBrand);
    $('#photo-form').addEventListener('submit', savePhoto);
    $('#photo-list').addEventListener('click', handlePhotoAction);
    $('#photo-cancel-edit').addEventListener('click', resetPhotoForm);
    $('#status-form').addEventListener('submit', saveStatus);
    document.querySelectorAll('[data-close]').forEach((button) => {
        button.addEventListener('click', () => $(`#${button.dataset.close}`).close());
    });
    loadDashboard();
});
