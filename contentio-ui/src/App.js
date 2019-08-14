import React from "react";
import { Route, Switch } from "react-router-dom";

import Home from "./components/Home";
import ProjectsPage from "./components/ProjectsPage";
import ProjectDetails from "./components/ProjectDetails";

const App = () => {
  return (
    <Switch>
      <Route exact path="/" component={Home} />
      <Route exact path="/projects" component={ProjectsPage} />
      <Route path="/projects/:projectId" component={ProjectDetails} />
      <Route render={() => <div>404</div>} />
    </Switch>
  );
};

export default App;
