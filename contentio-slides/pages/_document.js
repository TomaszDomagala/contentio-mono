//Code for Styled Components integration 
//https://dev.to/aprietof/nextjs--styled-components-the-really-simple-guide----101c

import Document, { Head, Main, NextScript } from 'next/document';
import { ServerStyleSheet } from 'styled-components';
export default class MyDocument extends Document {

    static getInitialProps({ renderPage }) {

        const sheet = new ServerStyleSheet();
        const page = renderPage((App) => (props) =>
            sheet.collectStyles(<App {...props} />),
        );
        const styleTags = sheet.getStyleElement();
        return { ...page, styleTags };
    }

    render() {
        return (
            <html>
                <Head>
                    {this.props.styleTags}
                </Head>
                <body style={{ margin: 0, minHeight: "100vh", minWidth: "100vw" }}>
                    <Main />
                    <NextScript />
                </body>
            </html>
        );
    }
}