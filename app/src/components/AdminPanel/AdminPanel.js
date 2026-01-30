import { useEffect, useState } from "react";
import { useAuth } from "../AuthContext";
import "./AdminPanel.css";

function AdminPanel() {
    const { authenticatedUser } = useAuth();
    const [users, setUsers] = useState([]);

    useEffect(() => {
        const fetchUsers = async () => {
            try {
                const response = await fetch("/api/admin/users", {
                    headers: {
                        Authorization: "Bearer " + authenticatedUser.token,
                    },
                });
                const data = await response.json();
                const filteredUsers = (Array.isArray(data) ? data : []).filter(u => u.role !== "ADMIN");
                setUsers(filteredUsers);
            } catch (err) {
                console.error("Ошибка загрузки пользователей:", err);
            }
        };
        fetchUsers();
    }, [authenticatedUser]);

    const handleRoleChange = (id, newRole) => {
        setUsers(users.map(u => u.id === id ? {...u, role: newRole} : u));
    };

    const updateRole = async (id) => {
        const user = users.find(u => u.id === id);
        try {
            await fetch(`/api/admin/users/${id}/role`, {
                method: "POST",
                headers: {
                    "Content-Type": "application/json",
                    Authorization: "Bearer " + authenticatedUser.token,
                },
                body: JSON.stringify({ role: user.role }),
            });
            alert("Роль обновлена");
        } catch (err) {
            console.error(err);
        }
    };

    const deleteUser = async (id) => {
        if (!window.confirm("Вы уверены, что хотите удалить этого пользователя?")) return;

        try {
            const response = await fetch(`/api/admin/users/${id}/delete`, {
                method: "POST", // вместо DELETE
                headers: {
                    "Authorization": "Bearer " + authenticatedUser.token,
                },
            });

            if (!response.ok) throw new Error("Не удалось удалить пользователя");

            setUsers(users.filter(user => user.id !== id));
        } catch (err) {
            console.error(err);
        }
    };


    return (
        <div className="admin-panel container">
            <h2>Управление пользователями</h2>
            <div className="table-responsive">
                <table className="table table-hover table-striped align-middle">
                    <thead className="table-dark">
                    <tr>
                        <th>Логин</th>
                        <th>Статус</th>
                        <th>Роль</th>
                        <th>Действия</th>
                    </tr>
                    </thead>
                    <tbody>
                    {Array.isArray(users) && users.map(user => (
                        <tr key={user.id}>
                            <td>{user.username}</td>
                            <td>
                                <span className="badge bg-success">Разблокирован</span>
                            </td>
                            <td>
                                <div className="d-flex align-items-center gap-2">
                                    <select
                                        className="form-select form-select-sm"
                                        value={user.role}
                                        onChange={e => handleRoleChange(user.id, e.target.value)}
                                    >
                                        <option value="AGENT">Агент</option>
                                        <option value="OWNER">Продавец</option>
                                        <option value="CUSTOMER">Покупатель</option>
                                    </select>
                                    <button className="btn btn-primary btn-sm" onClick={() => updateRole(user.id)}>
                                        Обновить
                                    </button>
                                </div>
                            </td>
                            <td>
                                <button className="btn btn-primary btn-sm" onClick={() => deleteUser(user.id)}>
                                    Удалить
                                </button>
                            </td>
                        </tr>
                    ))}
                    </tbody>
                </table>
            </div>
        </div>
    );
}

export default AdminPanel;
