import httpProxy from 'http-proxy';

const target = 'http://3.6.254.66:6000';

const proxy = httpProxy.createProxyServer({
  target: target,
  changeOrigin: true,
});

export const config = {
  api: {
    bodyParser: false,
    externalResolver: true,
  },
};

export default function handler(req, res) {
  let path = req.url;

  if (path.startsWith('/api/') || path.startsWith('/admin/') || path.startsWith('/get/')) {
    req.url = `/AUTHENTICATIONSYSTEM${path}`;
  }

  proxy.web(req, res, { target: target }, (err) => {
    console.error('Proxy Error:', err);
    res.status(502).send('Proxy error: ' + err.message);
  });
}
