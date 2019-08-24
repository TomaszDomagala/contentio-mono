import { createStore, combineReducers, applyMiddleware } from "redux";
import thunkMiddleware from "redux-thunk";
import { composeWithDevTools } from "redux-devtools-extension";
import { connectRouter } from "connected-react-router";
import { createBrowserHistory } from "history";

import { projectsReducer } from "./projects/reducer";
import { projectViewReducer } from "./projectview/reducer";
import { submissionViewReducer } from "./submissionview/reducer";

export const history = createBrowserHistory();

const rootReducer = combineReducers({
	router: connectRouter(history),
	projectsReducer,
	projectViewReducer,
	submissionViewReducer
});

function configureStore() {
	const middlewares = [thunkMiddleware];
	const middleWareEnhancer = applyMiddleware(...middlewares);

	const store = createStore(
		rootReducer,
		composeWithDevTools(middleWareEnhancer)
	);

	return store;
}

const store = configureStore();
export default store;
export const dispatch = store.dispatch;
