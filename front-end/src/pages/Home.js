import React from 'react';
import '../styles/home.css';

const Home = () => {
  return (
    <div className="home">
        <h1 className="home-title">Добре дошли в SmartQuiz</h1>
        <div className="home-content">
            <div className="info-box">
                <h2 className="info-title">Тестовете</h2>
                <p className="info-text">
                    Нашата система за тестове е специално създадена за училищна употреба, предоставяйки множество
                    възможности за персонализиране. Учениците могат да решават тестове по различни предмети, като
                    получават подробна обратна връзка и оценки.
                </p>
            </div>
            <div className="info-box">
                <h2 className="info-title">Куизовете</h2>
                <p className="info-text">
                    Ако търсите забавление, нашите куизове са точно за вас! В тях можете да се изкачвате по нива, да
                    събирате награди и да се забавлявате, решавайки въпроси на необичайни и интересни теми.
                </p>
            </div>
            <p className="home-footer">
                Разгледайте, решавайте и се забавлявайте с нашата уникална система за тестове и куизове!
            </p>
        </div>
    </div>
  );
};

export default Home;