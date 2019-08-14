import { SET_PROJECT_PAGE } from "./types";

const initialState = {
  projectsPage: {
    content: []
  }
};

export function projectsReducer(state = initialState, action) {
  switch (action.type) {
    case SET_PROJECT_PAGE: {
      return { ...state, projectsPage: action.page };
    }

    default: {
      return state;
    }
  }
}
