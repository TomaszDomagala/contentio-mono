const express = require("express");
const next = require("next");
const fs = require("fs");
const puppeteer = require("puppeteer");
const mkdirp = require("mkdirp");
const bodyParser = require("body-parser");

const port = parseInt(process.env.PORT, 10) || 3030;
const dev = process.env.NODE_ENV !== "production";
const app = next({ dev });
const handle = app.getRequestHandler();

// let browser;
let browsers = [];
const maxBrowserNumber = 3;
let currentBrowserNumber = 0;

const requestPage = async () => {
	const browser = browsers[currentBrowserNumber];
	currentBrowserNumber = (currentBrowserNumber + 1) % maxBrowserNumber;
	return await browser.newPage();
};

const createScreenshot = async (submissionId, slideNumber) => {
	const page = await requestPage();
	await page.setViewport({ width: 1920, height: 1080 });
	const url = `http://127.0.0.1:3030/askreddit/submission/${submissionId}/${slideNumber}`;
	await page.goto(url);
	const img = await page.screenshot({ encoding: "base64" });
	await page.close();
	return img;
};

(async () => {
	for (let i = 0; i < maxBrowserNumber; i++) {
		const browser = await puppeteer.launch();
		browsers.push(browser);
	}
	// browser = await puppeteer.launch();
	await app.prepare();
	const server = express();

	server.use(bodyParser.urlencoded({ extended: true }));
	server.use(bodyParser.json());

	server.get("/askreddit/submission/:id/:slide", (req, res) => {
		return app.render(req, res, "/askreddit/submission", {
			id: req.params.id,
			slide: req.params.slide
		});
	});

	server.get("/api/screenshot/:id/:slide", async (req, res) => {
		const { id, slide } = req.params;
		const img = await createScreenshot(id, slide);
		const imgBuffer = Buffer.from(img, "base64");

		res.writeHead(200, {
			"Content-Type": "image/png",
			"Content-Length": imgBuffer.length
		});

		return res.end(imgBuffer);
	});

	server.get("*", (req, res) => {
		return handle(req, res);
	});

	server.listen(port, err => {
		if (err) throw err;
		console.log(`> Ready on http://localhost:${port}`);
	});
})();
