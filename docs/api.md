| 操作   | 英文动词          | HTTP 方法 | 端点                           | 参数位置                        |
| ---- | ------------- | ------- |------------------------------| --------------------------- |
| 列表查询 | `list`        | GET     | `/api/v1/users/list`         | `?page=&size=&keyword=&…`   |
| 单条详情 | `detail`      | GET     | `/api/v1/users/detail`       | `?id=1001`                  |
| 创建   | `create`      | POST    | `/api/v1/users/create`       | Body: `{ username, … }`     |
| 全量更新 | `update`      | POST    | `/api/v1/users/update`       | Body: `{ id, username, … }` |
| 局部更新 | `modify`      | POST    | `/api/v1/users/modify`       | Body: `{ id, email, … }`    |
| 删除   | `delete`      | POST    | `/api/v1/users/delete`       | Body: `{ id }`              |
| 批量创建 | `batchCreate` | POST    | `/api/v1/users/create/batch` | Body: `{ items:[…] }`       |
| 批量删除 | `batchDelete` | POST    | `/api/v1/users/delete/batch` | Body: `{ ids:[…] }`         |
| 导出   | `export`      | POST    | `/api/v1/users/export`       | Body: `{ filter:{…} }`      |
| 导入   | `import`      | POST    | `/api/v1/users/import`       | multipart/form-data         |
