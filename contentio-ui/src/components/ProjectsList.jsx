import React, { Component } from 'react'
import { connect } from "react-redux";
import { Box } from 'rebass'
import { getProjectsPage } from '../store/projects/actions'
import ProjectListItem from './ProjectListItem'


class ProjectsList extends Component {


    componentDidMount() {
        this.props.requestPage(0)
    }

    render() {
        return (
            <Box mt={3}>
                {this.props.projectsPage.content.map(project => <ProjectListItem key={project.id} project={project} />)}
            </Box>
        )
    }
}



const mapStateToProps = (state) => ({
    projectsPage: state.projectsReducer.projectsPage
})

const mapDispatchToProps = dispatch => ({
    requestPage: (page) => dispatch(getProjectsPage(page))
})


export default connect(mapStateToProps, mapDispatchToProps)(ProjectsList)