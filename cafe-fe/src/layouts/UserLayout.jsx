import Header from '../components/Header';

const UserLayout = ({ children }) => {
  return (
    <div className="min-h-screen bg-gray-50">
      <Header />
      <div className="pt-16">
        {children}
      </div>
    </div>
  );
};

export default UserLayout;