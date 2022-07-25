//Install express server
const express = require('express');
const path = require('path');
const middleware = require('http-proxy-middleware');

const app = express();

// Proxy api to a backend server.
const apiProxy = middleware.createProxyMiddleware({ target: process.env.API_HOST, changeOrigin: true });

app.use('/api/v1', apiProxy);

// Serve only the static files form the dist directory
app.use(express.static('./dist/frontend'));

app.get('/*', (req, res) =>
    res.sendFile('index.html', { root: 'dist/frontend/' }),
);

// Start the app by listening on the default Heroku port
app.listen(process.env.PORT || 8080);