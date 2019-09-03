import App, { Container } from "next/app";
import { ThemeProvider } from "styled-components";
import theme from "../utils/theme.js";
import Head from "next/head";
import React from "react";

//Custom _app.js
//https://github.com/zeit/next.js#custom-app
//https://github.com/zeit/next.js/blob/master/errors/opt-out-automatic-prerendering.md

export default class MyApp extends App {
	render() {
		const { Component, pageProps } = this.props;

		return (
			<Container>
				<Head>
					<title>Contetnio!</title>

					<link
						href="https://fonts.googleapis.com/css?family=Noto+Sans&display=swap"
						rel="stylesheet"
					/>
					<link
						href="https://fonts.googleapis.com/css?family=IBM+Plex+Sans&display=swap"
						rel="stylesheet"
					/>
					<link
						href="http://127.0.0.1:3030/static/redditfont.css"
						rel="stylesheet"
					/>
				</Head>
				<ThemeProvider theme={theme}>
					<Component {...pageProps} />
				</ThemeProvider>
			</Container>
		);
	}
}
