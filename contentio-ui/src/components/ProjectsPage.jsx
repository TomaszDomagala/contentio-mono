import React, { Component } from 'react'
import { Box } from 'rebass'
import { connect } from "react-redux";
import ProjectsList from './ProjectsList';
import CreateProject from './CreateProject';

class ProjectsPage extends Component {

    render() {
        return (
            <Box bg='background' style={{ minHeight: '100vh' }}>
                <Box p={3}  mx='auto' width={[1, 2 / 3, null, 2 / 5]}>
                    <CreateProject />
                    <ProjectsList />
                </Box>
            </Box>

        )
    }

}

const mapDispatchToProps = dispatch => ({
})

export default connect(null, mapDispatchToProps)(ProjectsPage)