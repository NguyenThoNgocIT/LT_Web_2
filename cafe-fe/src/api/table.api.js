import api from './axiosConfig';

// ADMIN endpoints
export const getAllTables = async () => {
    const res = await api.get('/admin/tables');
    return res.data;
};

export const getTablesByStatus = async (status) => {
    const res = await api.get(`/admin/tables/status/${status}`);
    return res.data;
};

export const createTable = async (tableData) => {
    // Backend expects array of tables
    const payload = Array.isArray(tableData) ? tableData : [tableData];
    const res = await api.post('/admin/tables', payload);
    return res.data;
};

export const updateTable = async (id, tableData) => {
    const res = await api.put(`/admin/tables/${id}`, tableData);
    return res.data;
};

export const updateTableStatus = async (id, status) => {
    // Backend expects { status: "AVAILABLE" | "RESERVED" | "OCCUPIED" | "COMPLETED" }
    const res = await api.put(`/admin/tables/${id}/status`, { status });
    return res.data;
};

export const deleteTable = async (id) => {
    const res = await api.delete(`/admin/tables/${id}`);
    return res.data;
};

// USER endpoints
export const getAvailableTables = async () => {
    const res = await api.get('/user/tables/available');
    return res.data;
};

// Reservation helpers (for USER)
export const createReservation = async (reservationData) => {
    const res = await api.post('/user/reservations', reservationData);
    return res.data;
};

export const getMyReservations = async () => {
    const res = await api.get('/user/reservations/my');
    return res.data;
};

export const cancelReservation = async (id) => {
    const res = await api.put(`/user/reservations/${id}/cancel`);
    return res.data;
};

export default { 
    getAllTables, 
    getTablesByStatus,
    createTable, 
    updateTable, 
    updateTableStatus,
    deleteTable,
    getAvailableTables,
    createReservation,
    getMyReservations,
    cancelReservation
};