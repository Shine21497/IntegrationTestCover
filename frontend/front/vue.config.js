module.exports = {
    devServer: {
        proxy: {
            '/apiurl': {
                target: 'http://localhost:8080/',
                changeOrigin: true,
                pathRewrite: {
                    '^/apiurl': '/'
                }
            }
        },
        port: 8088
    }
}