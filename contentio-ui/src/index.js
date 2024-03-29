import React from 'react';
import ReactDOM from 'react-dom';
import './index.css';
import App from './App';


import { ThemeProvider } from 'styled-components';
import * as serviceWorker from './serviceWorker';
import { ConnectedRouter } from 'connected-react-router'
import { BrowserRouter } from "react-router-dom";
import { Provider } from "react-redux";
import store, { history } from "./store/intex";
import theme from './utils/theme'

const Root = ()=>(
<Provider store={store}>
        <ThemeProvider theme={theme}>
            <ConnectedRouter history={history}>
                <BrowserRouter>
                    <App />
                </BrowserRouter>
            </ConnectedRouter>
        </ThemeProvider>

    </Provider>
);

ReactDOM.render(<Root />, document.getElementById('root'));

// If you want your app to work offline and load faster, you can change
// unregister() to register() below. Note this comes with some pitfalls.
// Learn more about service workers: https://bit.ly/CRA-PWA
serviceWorker.unregister();
