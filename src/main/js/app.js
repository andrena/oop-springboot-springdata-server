'use strict';

const React = require('react');
const ReactDOM = require('react-dom');
const client = require('./client');

class App extends React.Component {

    constructor(props) {
        super(props);
        this.state = {events: []};
    }

    componentDidMount() {
        client({method: 'GET', path: '/event'}).done(response => {
            this.setState({events: response.entity._embedded.events});
    })
    }

    render() {
        return (
            <EventList events={this.state.events}/>
        )
    }
}

class EventList extends React.Component {
    render() {
        var events = this.props.events.map(event =>
            <Event key={event._links.self.href} event={event}/>
        );
        return (
            <table className="table table-striped">
                <tbody>
                <tr>
                    <th>Title</th>
                    <th>Description</th>
                    <th>Start Time</th>
                    <th>End Time</th>
                    <th>Room</th>
                    <th>Track</th>
                    <th>Speakers</th>
                </tr>
                {events}
                </tbody>
            </table>
        )
    }
}

class Event extends React.Component {
    render() {
        return (
            <tr>
                <td>{this.props.event.title}</td>
                <td>{this.props.event.description}</td>
                <td>{this.props.event.startTime}</td>
                <td>{this.props.event.endTime}</td>
                <td>{this.props.event.room}</td>
                <td>{this.props.event.track}</td>
                <td>{getSpeakers()}</td>
            </tr>
        )
    }
}

function getSpeakers() {
    //TODO: get projections
    return "speaker TODO";
}

ReactDOM.render(
    <App />,
    document.getElementById('react')
);